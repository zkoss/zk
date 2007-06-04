/* Context.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2007 7:15:37 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;

/**
 * UiFactory Connection Context.
 * 
 * @author henrichen
 *
 */
public class Context {
	HttpConnection _conn;
	String _prefix;
	Display _disp;
	
	public Context(HttpConnection conn, Display disp) {
		_conn = conn;
		_disp = disp;
	}
	
	/**
	 * Prefix the url with current connection if a relative url path.
	 * @param url the url to be prefix.
	 * @return prefix url.
	 */
	public String prefixURL(String url) {
		if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
			url = getPrefix() + url;
		}
		return url;
	}
	
	/**
	 * Get the Mobile Display
	 * @return the mobile display
	 */
	public Display getDisplay() {
		return _disp;
	}
	
	private String getPrefix() {
		if (_prefix == null) {
			_prefix = _conn.getProtocol()+"://"+_conn.getHost()+(_conn.getPort() != 80 ? (":"+_conn.getPort()) : "");
		}
		return _prefix;
	}
}
