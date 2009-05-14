/* SerializableUiFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 12:38:04     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.http;

import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;

/**
 * The serializable implementation of {@link org.zkoss.zk.ui.sys.UiFactory}.
 * The instances returned by {@link #newSession} is serializable, such that
 * session can be stored when the Web server stops and restore after it starts.
 * 
 * @author tomyeh
 * @since 5.0.0
 */
public class SerializableUiFactory
extends org.zkoss.zk.ui.http.SerializableUiFactory {
	/** Called by WebAppInit to initialize at application startup.
	 */
	public static void init() { //for backward compatibility
		_uiFactoryClass = SerializableUiFactory.class;
	}

	//super//
	public Session newSession(WebApp wapp, Object nativeSess, Object request) {
		return new SerializableSession(wapp, (HttpSession)nativeSess, request);
	}
}
