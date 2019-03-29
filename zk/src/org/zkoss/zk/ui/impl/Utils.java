/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 23 19:17:35 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import static org.zkoss.lang.Generics.cast;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.resource.XMLResourcesLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.ShadowInfo;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Composer;

/**
 * Utilities to implement ZK.
 * @author tomyeh
 * @since 5.0.7
 */
public class Utils {
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	/** Marks the per-desktop information of the given key will be generated,
	 * and returns true if the information is not generated yet
	 * (i.e., this method is NOT called with the given key).
	 * You could use this method to minimize the bytes to be sent to
	 * the client if the information is required only once per desktop.
	 */
	public static boolean markClientInfoPerDesktop(Desktop desktop, String key) {
		return !(desktop instanceof DesktopImpl) //always gen if unknown
				|| ((DesktopImpl) desktop).markClientInfoPerDesktop(key);
	}

	/** Returns the XML resources locator to locate
	 * metainfo/zk/config.xml, metainfo/zk/lang.xml, and metainfo/zk/lang-addon.xml
	 */
	public static XMLResourcesLocator getXMLResourcesLocator() {
		if (_xmlloc == null) {
			final String clsnm = Library.getProperty("org.zkoss.zk.ui.sys.XMLResourcesLocator.class");
			if (clsnm != null) {
				try {
					return _xmlloc = (XMLResourcesLocator) Classes.newInstanceByThread(clsnm);
				} catch (Throwable ex) {
					log.warn("Unable to load " + clsnm, ex);
				}
			}
			_xmlloc = new ClassLocator();
		}
		return _xmlloc;
	}

	private static XMLResourcesLocator _xmlloc;

	/** Instantiates a composer of the given object.
	 * This method will invoke {@link org.zkoss.zk.ui.sys.UiFactory#newComposer}
	 * to instantiate the composer if page is not null.
	 * @param page the page that the composer will be created for.
	 * Ignored if null.
	 * @param o the composer instance, the class of the composer to instantiate,
	 * or the name of the class of the composer.
	 * If <code>o</code> is an instance of {@link Composer}, it is returned
	 * directly.
	 */
	public static Composer newComposer(Page page, Object o) throws Exception {
		Class cls;
		if (o instanceof String) {
			final String clsnm = ((String) o).trim();
			if (page != null)
				return ((WebAppCtrl) page.getDesktop().getWebApp()).getUiFactory().newComposer(page, clsnm);
			cls = Classes.forNameByThread(clsnm);
		} else if (o instanceof Class) {
			cls = (Class) o;
			if (page != null)
				return ((WebAppCtrl) page.getDesktop().getWebApp()).getUiFactory().newComposer(page, cls);
		} else
			return (Composer) o;

		return (Composer) cls.newInstance();
	}

	/** Returns the component info associated with the given component, or null
	 * if not available.
	 * <p>It is used only internally.
	 */
	public static ComponentInfo getComponentInfo(Component comp) {
		final Map<Component, ComponentInfo> map = getComponentInfos(false);
		return map != null ? map.get(comp) : null;
	}

	private static Map<Component, ComponentInfo> getComponentInfos(boolean autoCreate) {
		Execution exec = Executions.getCurrent();
		if (exec == null)
			return null;

		Map<Component, ComponentInfo> result = cast((Map) exec.getAttribute(COMPONENT_INFO));
		if (result == null && autoCreate) {
			result = new HashMap<Component, ComponentInfo>();
			exec.setAttribute(COMPONENT_INFO, result);
		}
		return result;
	}

	/** Sets the component info for the given component.
	 * <p>It is used only internally.
	 */
	public static void setComponentInfo(Component comp, ComponentInfo info) {
		final Map<Component, ComponentInfo> map = getComponentInfos(info != null);
		if (map != null)
			if (info != null)
				map.put(comp, info);
			else
				map.remove(comp);
	}

	private static Map<Component, ShadowInfo> getShadowInfos(boolean autoCreate) {
		Execution exec = Executions.getCurrent();
		if (exec == null)
			return null;

		Map<Component, ShadowInfo> result = cast((Map) exec.getAttribute(COMPONENT_INFO));
		if (result == null && autoCreate) {
			result = new HashMap<Component, ShadowInfo>();
			exec.setAttribute(COMPONENT_INFO, result);
		}
		return result;
	}

	/** Tests if the given attribute is defined in a component or in library property.
	 * @param name the name of the attribute
	 * @param defValue the default value if neither component's attribute or library property is defined
	 * for the given name
	 * @param recurse whether to look up the ancestor's attribute
	 * @since 5.0.7
	 */
	public static final boolean testAttribute(Component comp, String name, boolean defValue, boolean recurse) {
		Object val = comp.getAttribute(name, recurse);
		if (val == null)
			val = Library.getProperty(name);
		return val instanceof Boolean ? ((Boolean) val).booleanValue() : val != null ? "true".equals(val) : defValue;
	}

	/** Sets the component info for the given component.
	 * <p>It is used only internally.
	 * @since 8.0.0
	 */
	public static void setShadowInfo(Component comp, ShadowInfo info) {
		final Map<Component, ShadowInfo> map = getShadowInfos(info != null);
		if (map != null)
			if (info != null)
				map.put(comp, info);
			else
				map.remove(comp);
	}

	private static final String COMPONENT_INFO = "org.zkoss.zk.ui.metainfo.compinfo";
}
