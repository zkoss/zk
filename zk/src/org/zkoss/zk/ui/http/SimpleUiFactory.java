/* SimpleUiFactory.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 12:29:48     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import javax.servlet.http.HttpSession;
import javax.portlet.PortletSession;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.impl.AbstractUiFactory;

/**
 * The default implementation of {@link org.zkoss.zk.ui.sys.UiFactory}.
 *
 * @author tomyeh
 */
public class SimpleUiFactory extends AbstractUiFactory {
	public Session newSession(WebApp wapp, Object nativeSess,
	Object request) {
		if (nativeSess instanceof HttpSession)
			return new SimpleSession(wapp, (HttpSession)nativeSess, request);
		else
			return new SimpleSession(wapp, (PortletSession)nativeSess, request);
	}
}
