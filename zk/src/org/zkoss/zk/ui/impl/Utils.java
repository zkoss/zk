/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 23 19:17:35 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.XMLResourcesLocator;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * Utilities to implement ZK.
 * @author tomyeh
 * @since 5.5.0
 */
public class Utils {
	private static final Log log = Log.lookup(Utils.class);

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
			cls = page != null ? page.resolveClass((String)o):
				Classes.forNameByThread(((String)o).trim());
		} else if (o instanceof Class) {
			cls = (Class)o;
		} else
			return (Composer)o;

		if (page != null) 
			return ((WebAppCtrl)page.getDesktop().getWebApp()).getUiFactory().newComposer(cls, page);
		return (Composer)cls.newInstance();
	}
}
