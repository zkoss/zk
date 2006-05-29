/* PerDomainManager.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 18 16:11:40     2003, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.sys;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;

import com.potix.mesg.MCommon;
import com.potix.lang.Classes;
import com.potix.lang.Exceptions;
import com.potix.lang.SystemException;
import com.potix.util.logging.Log;
import com.potix.util.prefs.Apps;

/**
 * A manager who manages singlton-per-domain (SPD) managers.
 * In other words, it stores the SPD managers in per-domain cache, such
 * that there is, for each type, only one manager per domain.
 *
 * <p>Usage:
 *<pre><code>class Manager {
	public static Manager the() {
		final PerDomainManager pdm = PerDomainManager.the();
		final Manager one = (Manager)pdm.get(Manager.class); //a tag class
		if (one != null)
			return one:
		try {
			return (Manager)pdm.newInstance(
				Manager.class, //a tag class
				MyManager.class); //the real class to instantiate
		} catch(Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
 }</code></pre>
 *
 * <p>Note: if the real class implements {@link AutoStart}, then its
 * {@link AutoStart#start} will be invoked when the singleton is instantiated.
 *
 * <p>Design issue: why not merge {@link #get} and {@link #newInstance(Class, Class)}.
 * Reason: performance consideration. For each domain, a manager is
 * initialized only once. It means most of them invoke get() only.
 * Note: the real class is usually retrieved from i3-prefs.xml.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.13 $ $Date: 2006/05/29 04:27:25 $
 * @see AutoStart
 * @see Singleton
 */
public class PerDomainManager {
	private static final Log log = Log.lookup(PerDomainManager.class);

	/** the singleton-per-domain manager. */
	private static final Singleton _the = new Singleton();
	/** The service name. */
	private static final String SERVICE_NAME = "per-domain";

	/** a map of (String domain, Map(tagClass, manager)). */
	protected final Map _domains = new HashMap(7);

	/** Returns the manager. */
	public static PerDomainManager the() {
		final PerDomainManager one = (PerDomainManager)_the.get();
		if (one != null)
			return one;

		try {
			return (PerDomainManager)_the.newInstance(
				Apps.getProperty(
					"com.potix.util.sys.PerDomainManager.class",
					PerDomainManager.class.getName()));
		}catch(Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
	public PerDomainManager() {
		if (_the.get() != null)
			throw new SystemException(MCommon.SERVICE_INIT_TWICE_NOT_ALLOWED, SERVICE_NAME);
		log.info(MCommon.SERVICE_INIT_OK, SERVICE_NAME);
	}

	/** Returns the manager by specifying a class, or null if not found.
	 * The specified class is called a tag class. It is usually the base class.
	 * It must be the same as the tagClass argument of {@link #newInstance(Class, Class)}
	 *
	 * <p>The caller has no need to synchronize, because this method is
	 * thread-safe.
	 *
	 * <p>Implementation: if you store all managers in one map,
	 * you don't need to override this method. Rather, override
	 * {@link #getManagers}.
	 *
	 * @see #newInstance(Class, Class)
	 * @see #newInstance(Class, String)
	 */
	public Object get(Class tagClass) {
		final Map managers = getManagers();
		synchronized (managers) {
			final Object o = managers.get(tagClass);
			return o instanceof Starter ? null: o;
				//null if Starter, so newInstance will be called
		}
	}
	/** Holds the singleton is executing {@link AutoStart#start}.
	 */
	private static class Starter {
		public Object the;
		private Starter(Object the) {
			this.the = the;
		}
	}
	/** Creates the manager by specifying a tag class and a real class.
	 *
	 * <p>The caller has no need to synchronize, because this method is
	 * thread-safe.
	 *
	 * <p>Implementation: if you store all managers in one map,
	 * you don't need to override this method. Rather, override
	 * {@link #getManagers}.
	 *
	 * @see #get
	 * @see #newInstance(Class, String)
	 */
	public Object newInstance(Class tagClass, Class realClass)
	throws InstantiationException, IllegalAccessException {
		final Map managers = getManagers();
		synchronized (managers) {
			Object o = managers.get(tagClass);
			if (o != null)
				return o instanceof Starter ? ((Starter)o).the: o;
					//reentrant from the same thread if Starter found

			o = realClass.newInstance();
			if (o instanceof AutoStart) {
				managers.put(tagClass, new Starter(o));
					//let reentrant from the same thread work
				try {
					((AutoStart)o).start();
				} finally {
					managers.remove(tagClass);
				}
			}
			managers.put(tagClass, o);
			return o;
		}
	}
	/** Creates the manager by specifying a tag class and a real class name.
	 *
	 * @see #get
	 * @see #newInstance(Class, Class)
	 */
	public Object newInstance(Class tagClass, String realClassName)
	throws InstantiationException, IllegalAccessException,
	NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
		return newInstance(tagClass, Classes.forNameByThread(realClassName));
	}

	/** Resets the manager by specifying a tag class for the current domain,
	 * and returns the current manager, if any.
	 * In other words, next call to {@link #get} will return null.
	 */
	public Object reset(Class tagClass) {
		final Map managers = getManagers();
		synchronized (managers) {
			if (managers.get(tagClass) instanceof Starter)
				throw new IllegalStateException("Cannot be called in start()");
			return managers.remove(tagClass);
		}
	}

	//-- utitlities --//
	/** Returns the managers for the current domain.
	 * The current domain is based on {@link Apps#getCurrentDomain}.
	 */
	protected Map getManagers() {
		final String domain = Apps.getCurrentDomain();
		assert domain != null;
		synchronized (_domains) {
			Map managers = (Map)_domains.get(domain);
			if (managers == null) {
				managers = new HashMap();
				_domains.put(domain, managers);
			}
			return managers;
		}
	}
}
