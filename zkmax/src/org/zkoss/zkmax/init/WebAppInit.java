/* WebAppInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 26 11:43:47     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.init;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zkmax.au.http.AuDownloader;

/**
 * The initialization of zkmax.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class WebAppInit implements org.zkoss.zk.ui.util.WebAppInit {
	public void init(WebApp wapp) throws Exception {
		AuDownloader.init(wapp);
	}
}
