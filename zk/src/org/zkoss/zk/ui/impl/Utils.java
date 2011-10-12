/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 23 19:17:35 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.XMLResourcesLocator;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * Utilities to implement ZK.
 * @author tomyeh
 * @since 5.0.7
 */
public class Utils {
	private static final Log log = Log.lookup(Utils.class);

	/** Marks the per-desktop information of the given key will be generated,
	 * and returns true if the information is not generated yet
	 * (i.e., this method is NOT called with the given key).
	 * You could use this method to minimize the bytes to be sent to
	 * the client if the information is required only once per desktop.
	 */
	public static
	boolean markClientInfoPerDesktop(Desktop desktop, String key) {
		return !(desktop instanceof DesktopImpl) //always gen if unknown
		|| ((DesktopImpl)desktop).markClientInfoPerDesktop(key);
	}

	/** Returns the XML resources locator to locate
	 * metainfo/zk/config.xml, metainfo/zk/lang.xml, and metainfo/zk/lang-addon.xml
	 */
	public static XMLResourcesLocator getXMLResourcesLocator() {
		if (_xmlloc == null) {
			final String clsnm = Library.getProperty("org.zkoss.zk.ui.sys.XMLResourcesLocator.class");
			if (clsnm != null) {
				try {
					return _xmlloc = (XMLResourcesLocator)Classes.newInstanceByThread(clsnm);
				} catch (Throwable ex) {
					log.warningBriefly("Unable to load "+clsnm, ex);
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
	public static Composer newComposer(Page page, Object o)
	throws Exception {
		Class cls;
		if (o instanceof String) {
			final String clsnm = ((String)o).trim();
			if (page != null)
				return ((WebAppCtrl)page.getDesktop().getWebApp())
					.getUiFactory().newComposer(page, clsnm);
			cls = Classes.forNameByThread(clsnm);
		} else if (o instanceof Class) {
			cls = (Class)o;
			if (page != null) 
				return ((WebAppCtrl)page.getDesktop().getWebApp())
					.getUiFactory().newComposer(page, cls);
		} else
			return (Composer)o;

		return (Composer)cls.newInstance();
	}
}
