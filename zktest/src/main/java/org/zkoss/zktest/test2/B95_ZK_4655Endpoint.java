/* B95_ZK_4655Endpoint.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 20 12:00:12 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.IOException;

import javax.websocket.EndpointConfig;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.http.ZKWebSocket;
import org.zkoss.zk.ui.sys.Storage;

/**
 * @author rudyhuang
 */
@ServerEndpoint(value = "/4655echo/", configurator = ZKWebSocket.class)
public class B95_ZK_4655Endpoint {
	private static final Logger LOG = LoggerFactory.getLogger(B95_ZK_4655Endpoint.class);

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		//since zk 8.6.4
		ZKWebSocket.initZkDesktop(session, config);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		Storage storage = ZKWebSocket.getDesktopStorage(session);
		if (storage == null) return;
		if ("receive".equals(message)) {
			Integer count = storage.getItem("count");
			try {
				session.getBasicRemote().sendText("Received..." + count);
			} catch (Exception e) {
				LOG.error("", e);
			}
		} else {
			try {
				storage.setItem("count", Integer.parseInt(message));
				session.getBasicRemote().sendText("Sent..." + message);
			} catch (IOException ex) {
				LOG.error("", ex);
			}
		}
	}

	@OnError
	public void handleError(Session session, Throwable t) {
		LOG.error("", t);
	}
}
