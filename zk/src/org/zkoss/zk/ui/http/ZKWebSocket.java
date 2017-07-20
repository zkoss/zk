/* ZKWebSocket.java

	Purpose:
		
	Description:
		
	History:
		6:10 PM 7/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.Storage;

/**
 * A web socket util class
 *
 * @author jumperchen
 * @since 8.0.0
 */
public final class ZKWebSocket extends ServerEndpointConfig.Configurator {

	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		org.zkoss.zk.ui.Session sess = SessionsCtrl.getSession(WebApps.getCurrent(), httpSession);
		config.getUserProperties().put(getClass().getName(), sess);
	}

	/**
	 * Returns a storage in desktop scope from the given websocket session.
	 *
	 * @param wsession websocket session
	 * @return a storage, null if desktop not found.
	 */
	public static Storage getDesktopStorage(Session wsession) {
		Desktop desktop = getDesktop(wsession);
		return desktop == null ? null : desktop.getStorage();
	}

	/**
	 * Returns the desktop from the given websocket session.
	 *
	 * @param wsession websocket session
	 * @return a desktop, null if desktop not found
	 */
	public static Desktop getDesktop(Session wsession) {
		org.zkoss.zk.ui.Session session = (org.zkoss.zk.ui.Session) wsession.getUserProperties()
				.get(ZKWebSocket.class.getName());
		if (session == null)
			throw new IllegalStateException("ZK Session cannot be null!");
		List<String> dtids = wsession.getRequestParameterMap().get("dtid");
		if (dtids.isEmpty())
			throw new IllegalStateException("the value of the key with 'dtid' cannot be null!");
		return ((SessionCtrl) session).getDesktopCache().getDesktopIfAny(dtids.get(0));
	}
}
