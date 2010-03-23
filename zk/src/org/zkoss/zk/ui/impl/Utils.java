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

/**
 * Utilities to implement ZK.
 * @author tomyeh
 * @since 5.1.0
 */
public class Utils {
	private static final Log log = Log.lookup(Utils.class);

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
}
