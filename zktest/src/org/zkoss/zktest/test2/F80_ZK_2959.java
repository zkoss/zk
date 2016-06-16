/* F80_ZK_2959.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 17 15:56:44 CST 2015, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.util.media.MediaTypeResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Image;

/**
 * 
 * @author wenning
 */
public class F80_ZK_2959 extends SelectorComposer<Component> implements MediaTypeResolver {

	@Wire
	Image img1;

	public String resolve(String format) {
		if ("svg".equals(format)) {
			WebApps.getCurrent().setAttribute("ZK2959", "ZK2959");
			return "image/svg+xml;";
		} else return null;
	}

	@Listen("onClick=#btn")
	public void handleBtn(){
		if ("ZK2959".equals(WebApps.getCurrent().getAttribute("ZK2959").toString())) {
			Clients.log("done");
			WebApps.getCurrent().removeAttribute("ZK2959");
		}
	}

}
