/* SimpleSessionCache.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 21 11:29:27     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionCache;

/**
 * A simple implementation of {@link SessionCache}.
 * @author tomyeh
 * @since 3.0.5
 */
public class SimpleSessionCache implements SessionCache {

	public void init(WebApp wapp) {
	}
	public void destroy(WebApp wapp) {
	}

	public void put(Session sess) {
		SessionAgent.agent.put(sess);
	}
	public Session get(Object navsess) {
		return SessionAgent.agent.get(navsess);
	}
	public void remove(Session sess) {
		SessionAgent.agent.remove(sess);
	}
}
