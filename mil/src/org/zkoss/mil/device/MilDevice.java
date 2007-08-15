/* MilDevice.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:26:50     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.mil.device;

import java.util.Map;
import java.util.Iterator;

import org.zkoss.mil.au.AuGoHome;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.device.Device;

/**
 * Represents the device supporting MIL (Mobile Interactive Language).
 *
 * @author tomyeh
 * @author henrichen
 */
public class MilDevice implements Device, java.io.Serializable {
	private static final long serialVersionUID = 200706080927L;
	private String _type;
	private String _uamsg;

	//Device//
	public String getType() {
		return _type;
	}
	public String getUnavailableMessage() {
		return _uamsg;
	}
	public void setUnavailableMessage(String unavailmsg) {
		_uamsg = unavailmsg;
	}

	public void init(String type, Desktop desktop, String unavailmsg) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type");
		_type = type;
		_uamsg = unavailmsg;
	}
	public void sessionWillPassivate(Desktop desktop) {
	}
	public void sessionDidActivate(Desktop desktop) {
	}

	public String getRawTagBegin(String tagname, Map props) {
		if (tagname == null)
			return "";

		final StringBuffer sb = new StringBuffer(80)
			.append('<').append(tagname);

		if (props != null)
			for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				HTMLs.appendAttribute(sb,
					Objects.toString(me.getKey()), Objects.toString(me.getValue()));
			}

		return sb.append('>').toString();
	}
	public String getRawTagEnd(String tagname) {
		return tagname != null ? "</" + tagname + ">\n": "";
	}
	
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
}
