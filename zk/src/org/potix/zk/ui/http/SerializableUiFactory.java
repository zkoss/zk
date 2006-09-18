/* SerializableUiFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 12:38:04     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.http;

import javax.servlet.http.HttpSession;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.impl.AbstractUiFactory;

/**
 * The serializable implementation of {@link com.potix.zk.ui.sys.UiFactory}.
 * The instances returned by {@link #newSession} is serializable, such that
 * session can be stored when the Web server stops and restore after it starts.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SerializableUiFactory extends AbstractUiFactory {
	public Session newSession(WebApp wapp, Object nativeSess,
	String clientAddr, String clientHost) {
		return new SerializableSession(
			wapp, (HttpSession)nativeSess, clientAddr, clientHost);
	}
}
