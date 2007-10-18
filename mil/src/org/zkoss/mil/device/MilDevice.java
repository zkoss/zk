/* MilDevice.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:26:50     2007, Created by henrihen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.mil.device;

import org.zkoss.mil.au.out.AuGoHome;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.device.GenericDevice;

/**
 * Represents the device supporting MIL (Mobile Interactive Language).
 *
 * @author henrichen
 */
public class MilDevice extends GenericDevice {
	/**
	 * Go to ZK Mobile home page. ZK Mobile home page is the main control page where end user 
	 * can input a new URL and visit a new web application.
	 *  
	 * @param url the default URL input in URL location. null means keep as is.
	 */
	public static void goHome(String url) {
		if (url != null && !url.startsWith("http://") && !url.startsWith("https://") 
		&& !url.startsWith("~.")) {
			url = Executions.getCurrent().getContextPath() + url;
		}
		addAuResponse(new AuGoHome(url));
	}
	
	private static final void addAuResponse(AuResponse response) {
		Executions.getCurrent()
			.addAuResponse(response.getCommand(), response);
	}

	//Device//
	public String getContentType() {
		return "text/xml;charset=UTF-8";
	}
}
