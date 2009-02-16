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
package org.zkoss.zk.ui.http;

import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.impl.AbstractUiFactory;

/**
 * The serializable implementation of {@link org.zkoss.zk.ui.sys.UiFactory}.
 * The instances returned by {@link #newSession} is serializable, such that
 * session can be stored when the Web server stops and restore after it starts.
 * 
 * @author tomyeh
 */
public class SerializableUiFactory extends AbstractUiFactory {
	public Session newSession(WebApp wapp, Object nativeSess, Object request) {
		return new SerializableSession(wapp, (HttpSession)nativeSess, request);
	}
}
