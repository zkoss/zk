/* ZKWebSocket.java

	Purpose:
		
	Description:
		
	History:
		6:10 PM 7/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.Storage;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;

/**
 * A web socket util class
 * @author jumperchen
 * @since 8.0.0
 */
final public class ZKWebSocket extends ServerEndpointConfig.Configurator {

	@Override public void modifyHandshake(ServerEndpointConfig config,
			HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		org.zkoss.zk.ui.Session sess = SessionsCtrl
				.getSession(WebApps.getCurrent(), httpSession);
		config.getUserProperties()
				.put(getClass().getName(), sess);
	}

	/**
	 * Returns a storage in desktop scope from the given websocket session.
	 * @param wsession websocket session
	 * @return a storage, never null.
	 */
	public static Storage getDesktopStorage(Session wsession) {
		org.zkoss.zk.ui.Session session = (org.zkoss.zk.ui.Session) wsession.getUserProperties()
				.get(ZKWebSocket.class.getName());
		if (session == null)
			throw new IllegalStateException("ZK Session cannot be null!");
		List<String> dtids = wsession.getRequestParameterMap().get("dtid");
		if (dtids.isEmpty())
			throw new IllegalStateException("the value of the key with 'dtid' cannot be null!");
		return ((SessionCtrl)session).getDesktopCache().getDesktop(dtids.get(0)).getStorage();
	}
}
