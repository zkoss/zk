/* Configuration.java

	Purpose:
		
	Description:
		
Copyright (C) 2014 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Classes;
import org.zkoss.lang.PotentialDeadLockException;
import org.zkoss.util.WaitLock;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.RichletConfigImpl;

/**
 * A {@link Richlet} container object is a component that contain active richlets and
 * their mappings to URL patterns.
 * 
 * <p>Richlets and their mappings added to a container are tracked in a map. They can be
 * added and removed at run-time.
 * 
 * <p>It should not be used directly, but rather should be used via {@link Configuration}.
 * 
 * @since 7.0.2
 */
final class RichletContainer {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/** 
	 * Map(String name, [Class richlet, Map params] or Richlet richlet). 
	 */
	private final Map<String, Object> _richlets = new HashMap<String, Object>();
	
	/**
	 * Map(String path, [String name, boolean wildcard]).
	 */
	private final Map<String, Object[]> _richletmaps = new HashMap<String, Object[]>();
	
	/** Adds the definition of a richlet.
	 *
	 * <p>If there was a richlet associated with the same name, the
	 * the old richlet will be replaced.
	 *
	 * @param name the richlet name
	 * @param params the initial parameters, or null if no initial parameter at all.
	 * Once called, the caller cannot access <code>params</code> any more.
	 * @return the previous richlet class or class-name with the specified name,
	 * or null if no previous richlet.
	 */
	public Object addRichlet(String name, Class<? extends Richlet> richletClass, Map<String, String> params) {
		if (richletClass == null)
			throw new IllegalArgumentException("A richlet class is required");

		return addRichlet0(name, richletClass, params);
	}
	/** Adds the definition of a richlet.
	 *
	 * <p>If there was a richlet associated with the same name, the
	 * the old servlet will be replaced.
	 *
	 * @param name the richlet name
	 * @param richletClassName the class name. The class will be loaded
	 * only when the richlet is loaded.
	 * @param params the initial parameters, or null if no initial parameter at all.
	 * Once called, the caller cannot access <code>params</code> any more.
	 * @return the previous richlet class or class-name with the specified name,
	 * or null if no previous richlet.
	 */
	public Object addRichlet(String name, String richletClassName, Map<String, String> params) {
		if (richletClassName == null || richletClassName.isEmpty())
			throw new IllegalArgumentException("richletClassName is required");

		return addRichlet0(name, richletClassName, params);
	}
	/** Adds the richlet.
	 *
	 * <p>If there was a richlet associated with the same name, the
	 * the old one will be replaced.
	 *
	 * @param name the richlet name
	 * @param richlet the richlet implemetation.
	 * @return the previous richlet class or class-name with the specified name,
	 * or null if no previous richlet.
	 */
	public Object addRichlet(String name, Richlet richlet) {
		if (richlet == null)
			throw new IllegalArgumentException("richlet instance is required");

		return addRichlet0(name, richlet, null);
	}

	private Object addRichlet0(String name, Object richletClass, Map<String, String> params) {
		Object o;
		
		for (;;) {
			// remove previous richlet if it exists
			o = removeRichlet0(name);
			
			synchronized (_richlets) {
				// add new richlet definition only if map does not contain record
				// with same name
				if (!(_richlets.containsKey(name))) {
					if (richletClass instanceof Richlet) {
						_richlets.put(name, richletClass);
					} else {
						_richlets.put(name, new Object[] {richletClass, params});
					}
					break;
				}
			}
		}

		return o;
	}

	/**
	 * Removes the richlet and associated richlet mappings.
	 * 
	 * @param name the richlet name
	 * @return the removed richlet class or class-name with the specified name,
	 * or null if the richlet is not found.
	 */
	public Object removeRichlet(String name) {
		// remove richlet
		final Object o = removeRichlet0(name);
		
		// remove associated richlet mappings
		removeRichletMapping(name);
		
		return o;
	}

	/**
	 * Removes the richlet.
	 * 
	 * @param name the richlet name
	 * @return the removed richlet class or class-name with the specified name,
	 * or null if the richlet is not found.
	 */
	private Object removeRichlet0(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name is required");
		}
		Object o;

		for (;;) {
			// remove richlet
			synchronized (_richlets) {
				o = _richlets.remove(name);
			}
			
			// verify it sth instancing richlet at the moment
			if (o instanceof WaitLock) {
				WaitLock lock = (WaitLock) o;
				if (!lock.waitUntilUnlock(300 * 1000)) { //5 minute
					String msg = new StringBuilder("Unable to remove richlet ").
							append(name).
							append("\nCause: conflict too long.").
							toString();
					final PotentialDeadLockException ex =
						new PotentialDeadLockException(msg);
					log.warn(msg, ex); //very rare, possibly a bug
					throw ex;
				}
			} else {
				break;
			}
		}
		
		if (o == null) {
			return null;
		}
		if (o instanceof Richlet) {
			// destroy object if it is richlet
			destroy((Richlet) o);
			return o.getClass();
		}
		return ((Object[])o)[0];
	}

	/** Adds a richlet mapping.
	 *
	 * @param name the name of the richlet.
	 * @param path the URL pattern. It must start with '/' and may end
	 * with '/*'.
	 * @exception UiException if the richlet is not defined yet.
	 * See {@link #addRichlet}.
	 */
	public void addRichletMapping(String name, String path) {
		//Note: "/" is the same as ""
		if (path == null || path.length() == 0 || "/".equals(path))
			path = "";
		else if (path.charAt(0) != '/')
			throw new IllegalArgumentException("path must start with '/', not "+path);

		final boolean wildcard = path.endsWith("/*");
		if (wildcard) //wildcard
			path = path.substring(0, path.length() - 2);
				//note it might be empty
		
		//richlet mapping cannot be added if richlet is not defined,
		//so check if richlet with same name exists and then
		//add richlet mapping
		synchronized (_richlets) {
			if (!_richlets.containsKey(name))
				throw new UiException("Richlet not defined: "+name);
		
			synchronized (_richletmaps) {
				_richletmaps.put(
					path, new Object[] {name, Boolean.valueOf(wildcard)});
			}
		}
	}

	/**
	 * Removes all richlet mappings for the specified richlet.
	 * 
	 * @param name the richlet name
	 */
	private void removeRichletMapping(String name) {
		// remove richlet mapping
		synchronized (_richletmaps) {
			Iterator<Map.Entry<String, Object[]>> iter = 
					_richletmaps.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Object[]> entry = iter.next();
				String richletName = (String) entry.getValue()[0];
				if (richletName.equals(name)) {
					iter.remove();
				}
			}
		}
	}
	
	/**
	 * Destroyes the richlet.
	 * 
	 * @param richlet the richlet to destroy
	 */
	private void destroy(Richlet richlet) {
		try {
			richlet.destroy();
		} catch (Throwable ex) {
			log.error("Unable to destroy "+richlet);
		}
	}

	/** Returns an instance of richlet of the specified name, or null
	 * if not found.
	 * 
	 * @param webApp the current web application
	 * @param name the richlet name
	 */
	@SuppressWarnings("unchecked")
	public Richlet getRichlet(WebApp webApp, String name) {
		WaitLock lock = null;
		final Object[] info;
		for (;;) {
			synchronized (_richlets) {
				Object o = _richlets.get(name);
				if (o == null || (o instanceof Richlet)) { //not found or loaded
					return (Richlet)o;
				} else if (o instanceof WaitLock) { //loading by another thread
					lock = (WaitLock)o;
				} else {
					info = (Object[])o;

					//going to load in this thread
					_richlets.put(name, lock = new WaitLock());
					break; //then, load it
				}
			} //sync(_richlets)

			if (!lock.waitUntilUnlock(300*1000)) { //5 minute
				final PotentialDeadLockException ex =
					new PotentialDeadLockException(
					"Unable to load richlet "+name+"\nCause: conflict too long.");
				log.warn("", ex); //very rare, possibly a bug
				throw ex;
			}
		} //for (;;)

		//load it
		try {
			if (info[0] instanceof String) {
				try {
					info[0] = Classes.forNameByThread((String)info[0]);
				} catch (Throwable ex) {
					throw new UiException("Failed to load "+info[0]);
				}
			}

			final Object o = ((Class<?>)info[0]).newInstance();
			if (!(o instanceof Richlet))
				throw new UiException(Richlet.class+" must be implemented by "+info[0]);

			final Richlet richlet = (Richlet)o;
			richlet.init(newRichletConfig(webApp, (Map<String, String>)info[1]));

			synchronized (_richlets) {
				_richlets.put(name, richlet);
			}
			return richlet;
		} catch (Throwable ex) {
			synchronized (_richlets) {
				_richlets.put(name, info); //remove lock and restore info
			}
			throw UiException.Aide.wrap(ex, "Unable to instantiate "+info[0]);
		} finally {
			lock.unlock();
		}
	}

	private RichletConfig newRichletConfig(WebApp webApp, Map<String, String> params) {
		return new RichletConfigImpl(webApp, params);
	}

	/** Returns an instance of richlet for the specified path, or
	 * null if not found.
	 * 
	 * @param webApp the current web application
	 * @param path the URL pattern. It must start with '/' and may end
	 * with '/*'.
	 */
	public Richlet getRichletByPath(WebApp webApp, String path) {
		if (path == null || path.length() == 0 || "/".equals(path))
			path = "";
		else if (path.charAt(0) != '/')
			path = '/' + path;

		final int len = path.length();
		for (int j = len;;) {
			final Richlet richlet =
				getRichletByPath0(webApp, path.substring(0, j), j != len);
			if (richlet != null || j == 0)
				return richlet;
			j = path.lastIndexOf('/', j - 1); //j must not -1
		}
	}

	private Richlet getRichletByPath0(WebApp webApp, String path, boolean wildcardOnly) {
		final Object[] info;
		synchronized (_richletmaps) {
			info = _richletmaps.get(path);
		}
		return info != null &&
			(!wildcardOnly || ((Boolean)info[1]).booleanValue()) ?
				getRichlet(webApp, (String)info[0]): null;
	}
	/** 
	 * Destroyes all richlets.
	 */
	public void detroyRichlets() {
		synchronized (_richlets) {
			for (Iterator<Object> it = _richlets.values().iterator(); it.hasNext();) {
				final Object o = it.next();
				if (o instanceof Richlet)
					destroy((Richlet)o);
			}
			_richlets.clear();
		}
	}
	
}
