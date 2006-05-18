/* ComponentManager.java

{{IS_NOTE
	$Id: ComponentManager.java,v 1.21 2006/05/18 04:06:31 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Sep 17 14:13:47     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.comp;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.potix.mesg.MCommon;
import com.potix.lang.D;
import com.potix.lang.Classes;
import com.potix.lang.SystemException;
import com.potix.util.prefs.Apps;
import com.potix.util.logging.Log;
import com.potix.util.resource.Locators;
import com.potix.util.resource.ClassLocator;
import com.potix.util.sys.Singleton;
import com.potix.util.sys.AutoStart;
import com.potix.idom.Document;
import com.potix.idom.Element;

/**
 * The component manager, which loads the definitions of components from
 * /metainfo/i3-comp.xml.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.21 $ $Date: 2006/05/18 04:06:31 $
 */
public class ComponentManager implements AutoStart {
	private static final Log log = Log.lookup(ComponentManager.class);

	/** The service name. */
	private static final String SERVICE_NAME = "Component Manager";
	/** The manager. */
	private static final Singleton _the = new Singleton();

	/** The definitions of component managers, (String feature, Info). */
	private final Map _defs = new HashMap();
	/** The components being loaded, (String feature, Map or Compoenent).
	 * If per-domain, a map is used.
	 */
	private final Map _comps = new HashMap();

	/** Returns the component manager.
	 */
	public static final ComponentManager the() {
		final ComponentManager one = (ComponentManager)_the.get();
		if (one != null)
			return one;

		try {
			return (ComponentManager)_the.newInstance(
				Apps.getProperty(
					"com.potix.comp.ComponentManager.class",
					ComponentManager.class.getName()));
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
	/** Constructor. Don't call this method directly. Rather, use {@link #the}.
	 */
	public ComponentManager() {
	}

	/** Initializes the component manager.
	 */
	public void start() {
		try {
			new Parser().parse();
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
		log.debug(MCommon.SERVICE_INIT_OK, SERVICE_NAME);
	}

	/** Returns whether a component is loaded yet (for the current domain if
	 * singleton-per-domain, or the whole system if singleton-per-system).
	 */
	public static final boolean isLoaded(String feature) {
		final ComponentManager one = (ComponentManager)_the.get();
		return one != null && one.isLoaded0(feature);
	}
	private final boolean isLoaded0(String feature) {
		final PerDomainMap byDomains;
		synchronized (_comps) {
			final Object o = _comps.get(feature);
			if (o instanceof PerDomainMap) {
				byDomains = (PerDomainMap)o;
				//fall thru
			} else {
				return o != null && !(o instanceof CompIniting);
			}
		}
		synchronized (byDomains) {
			final String domain = Apps.getCurrentDomain();
			final Object o = byDomains.get(domain);
			return o != null && !(o instanceof CompIniting);
		}
	}
	/** Gets a component of the specified feature.
	 */
	public final Object get(String feature) {
		final PerDomainMap byDomains;
		Info info = null;
		synchronized (_comps) {
			final Object o = _comps.get(feature);
			if (o instanceof PerDomainMap) {
				byDomains = (PerDomainMap)o;
				//fall thru
			} else if (o instanceof CompIniting) {
				return ((CompIniting)o).getComponent();
			} else if (o != null) {
				return o;
			} else {
				synchronized (_defs) {
					info = (Info)_defs.get(feature);
					if (info == null) {
						throw new SystemException("Undefined feature: "+feature+"\nCheck i3-comp.xml");
					}
				}

				if (info.perDomain) {
					byDomains = new PerDomainMap();
					_comps.put(feature, byDomains);
					//fall thru
				} else {
					//20041106: Tom Yeh: Performance will be better if we
					//could load outside of sync(_comps), but it is complex
					//to implement and not worth because it happens rarely
					return load(_comps, feature, info);
				}
			}
		}

		//Racing issue:
		//Because sync(byDomains) is not inside sync(_comps), it is possible
		//that register() modifies _comps when the following codes execute.
		//However, it is acceptable because the side effect is temporary
		//because byDomains won't be used again in this case
		synchronized (byDomains) {
			final String domain = Apps.getCurrentDomain();
			final Object comp = byDomains.get(domain);
			if (comp instanceof CompIniting) {
				return ((CompIniting)comp).getComponent();
			} else if (comp != null) {
				return comp;
			}

			if (info == null) {
				synchronized (_defs) {
					//20041106: Tom Yeh: Performance will be better if we
					//could load outside of sync(byDomains), but it is complex
					//to implement and not worth because it happens rarely
					info = (Info)_defs.get(feature);
				}
			}

			return load(byDomains, domain, info);
		}
	}
	/** Unloads a component for the specified feature.
	 * <p>Note: Unlike {@link #get}, all loaded component of the specified
	 * feature will be unloaded, including other domains.
	 */
	public final void unloadAll(String feature) {
		synchronized (_comps) {
			_comps.remove(feature);
		}
	}
	/** Loads a component. */
	private static Object load(Map map, Object key, Info info) {
		if (D.ON && log.finerable()) log.finer("Loading "+info);
		try {
			//1. put a mark (CompIniting) so we can detet it for reentry
			final CompIniting ii = new CompIniting();
			map.put(key, ii);

			//2. contruct and update to the mark
			final Object comp = Classes.newInstanceByThread(info.className);
			ii.setComponent(comp);
			if (comp instanceof Initial) {
				((Initial)comp).init(info.params);
			} else if (D.ON && !info.params.isEmpty()) {
				log.warning("com.potix.comp.Initial is not implemented, so parameters are ignored: "+info);
			}

			//3. put the real component back
			map.put(key, comp);
			return comp;
		} catch (Exception ex) {
			log.realCause(ex);
			throw SystemException.Aide.wrap(ex);
		}
	}

	/** Register the component for the specified feature by giving
	 * the class name. The class won't be loaded until the feature is requested
	 * by {@link #get}.
	 *
	 * <p>You rarely need to invoke this explicity. It is usually done by
	 * parsing i3-comp.xml.
	 *
	 * <p>If two or more invocations registers the same feature, the later
	 * overwrites the former. You might turn on DEBUG log to see what happens.
	 *
	 * @param feature the feature to serve
	 * @param className the class that implements the interface
	 * @param params the initial parameters.
	 */
	public void
	register(String feature, String className, boolean perDomain, Map params) {
		synchronized (_comps) {
			synchronized (_defs) {
				if (D.ON && log.finerable()) {
					final Info old = (Info)_defs.get(feature);
					if (old != null)
						log.finer("Overwrite "+feature+" replace "+old.className+" with "+className);
				}
				_defs.put(feature, new Info(feature, className, perDomain, params));
			}
			_comps.remove(feature); //enforce the manager to restart
		}
	}

	//-- info --//
	/** The information about a definition of a component (aka., feature).
	 */
	private static class Info {
		public final String feature;
		public final String className;
		public final Map params;
		public final boolean perDomain;
		private Info(String feature, String className, boolean perDomain, Map params) {
			if (feature == null || feature.length() == 0)
				throw new IllegalArgumentException("feature must be specified");
			if (className == null || className.length() == 0)
				throw new IllegalArgumentException("implementation-class must be specified");

			this.feature = feature;
			this.className = className;
			this.perDomain = perDomain;
			this.params = params == null || params.isEmpty() ?
				Collections.EMPTY_MAP: new HashMap(params);

			/* We don't load the class here since it might not exist
			   and eventually being overrided by following definition.
			if (D.ON) {
				//test whether the class is being defined
				try {
					Classes.forNameByThread(className);
				} catch (Exception ex) {
					throw SystemException.Aide.wrap(ex);
				}
			}*/
		}
		public String toString() {
			return "Feature: "+feature+" class="+className+" per-domain="+perDomain;
		}
	}
	/** To encapsulat what to store per domain components, such that
	 * a component could be even a HashMap instance.
	 */
	private static class PerDomainMap extends HashMap {
	}
	/** To detect dead loop and to allow get() to return the instance
	 * when Initial.init is executing.
	 */
	private static class CompIniting {
		private Object _comp;

		void setComponent(Object comp) {
			_comp = comp;
		}
		Object getComponent() {
			//20041106: Tom Yeh
			//If null, it means the constructor is running
			if (_comp == null)
				throw new IllegalStateException(
					"A dead loop occurs.\nPlease fix your component by implementing com.potix.comp.Initial");
			return _comp;
		}
	}

	//-- parsing --//
	/** Loads and parse all i3-comp.xml.
	 * <p>Implementation Note:<br>
	 * We don't use xawk because the component manager is a fundamental class
	 * and we don't want to let it depend on BeanShell
	 */
	private class Parser {
		/** Parses i3-comp.xml. */
		final void parse() throws Exception {
			final ClassLocator locator = new ClassLocator();
			final List xmls = locator.getDependentXmlResources(
				"metainfo/i3-comp.xml", "module-name", "depends");
			for (Iterator it = xmls.iterator(); it.hasNext();) {
				final Document doc = (Document)it.next();
				parseAll(doc.getRootElement());
			}

			//Note: we don't parse app/conf/i3-comp.xml or similar to
			//avoid the recursive dependency among App and ComponentManager
		}

		private final void parseAll(Element root) {
			for (Iterator it = root.getElements("component").iterator();
			it.hasNext();) {
				parseOne((Element)it.next());
			}
		}
		private final void parseOne(Element comp) {
			final String feature = getAndCheck(comp, "name");
			final String className = getAndCheck(comp, "implementation-class");
			final String perDomain = comp.getElementValue("singleton-per-domain", true);

			final Map params = new HashMap();
			for (Iterator it = comp.getElements("init-param").iterator();
			it.hasNext();) {
				final Element e = (Element)it.next();
				params.put(
					getAndCheck(e, "param-name"),
					getAndCheck(e, "param-value"));
			}

			register(feature, className,
				perDomain != null && !"false".equals(perDomain), params);
		}
		/** Returns the element value and throws SystemException if the value
		 * is null or empty.
		 *
		 * @param e the element that contains the string
		 * @param xml the element name that is expected
		 */
		private final String getAndCheck(Element e, String xml) {
			final String s = e.getElementValue(xml, true);
			if (s == null || s.length() == 0)
				throw new SystemException(MCommon.XML_ELEMENT_REQUIRED,
					new Object[] {xml, e.getLocator()});
			return s;
		}
	}
}
