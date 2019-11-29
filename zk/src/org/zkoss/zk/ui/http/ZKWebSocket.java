/* ZKWebSocket.java

	Purpose:
		
	Description:
		
	History:
		6:10 PM 7/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import java.util.List;
import java.util.Map;

import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.sys.DesktopCache;
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

	public static final String DESKTOP_ID_PARAM = "dtid";
	public static final String CONNECTION_UUID_PARAM = "connection_uuid";
	private static final String ZK_SESSION = ZKWebSocket.class.getName() + ".session";
	private static final String DESKTOP_ID = ZKWebSocket.class.getName() + ".dtid";

	/**
	 * Retrieves the current zk session from handshake request and store this temporarily under a unique key
	 * ('${desktopid}|[${connection_uuid}]') inside the endpoint config userProperties.
	 * This is temp key is removed during {@link #initZkDesktop(Session, EndpointConfig)}
	 *
	 * @param config
	 * @param request
	 * @param response
	 */
	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		org.zkoss.zk.ui.Session zkSession = SessionsCtrl.getSession(WebApps.getCurrent(), request.getHttpSession());
		Map<String, List<String>> parameterMap = request.getParameterMap();
		String tempSessionKey = tempSessionKey(extractDesktopId(parameterMap), extractConnectionUuid(parameterMap));
		config.getUserProperties().put(tempSessionKey, zkSession);
	}

	/**
	 * Stores the zksession and desktop id into the websocket session userProperties, removes the temporary key/value from the
	 * endpoint userProperties. After calling this method the zk desktop is available via {@link #getDesktop(Session)}.
	 * This method is called during websocket endpoint onOpen.
	 * E.g. {@link org.zkoss.zkmax.au.websocket.WebSocketEndPoint#onOpen(Session, EndpointConfig)}
	 *
	 * @param wsession
	 * @param config   Endpoint config
	 */
	public static void initZkDesktop(Session wsession, EndpointConfig config) {
		Map<String, List<String>> requestParameterMap = wsession.getRequestParameterMap();
		String desktopId = extractDesktopId(requestParameterMap);
		String connectionUuid = extractConnectionUuid(requestParameterMap);

		//remove key from config userProperties,
		//to avoid memory leaks in containers where endpoint config is a shared instance (e.g. in glassfish)
		org.zkoss.zk.ui.Session zkSession = (org.zkoss.zk.ui.Session)
				config.getUserProperties().remove(tempSessionKey(desktopId, connectionUuid));
		if (zkSession == null)
			throw new IllegalStateException("ZK Session cannot be null!");

		Map<String, Object> wsSessUserProperties = wsession.getUserProperties();
		wsSessUserProperties.put(ZK_SESSION, zkSession);
		wsSessUserProperties.put(DESKTOP_ID, desktopId);
	}

	/**
	 * extract mandatory desktop id parameter
	 */
	private static String extractDesktopId(Map<String, List<String>> requestParameterMap) {
		List<String> desktopIds = requestParameterMap.get(DESKTOP_ID_PARAM);
		if (desktopIds.isEmpty())
			throw new IllegalStateException("the value of the key with 'dtid' cannot be null!");
		return desktopIds.get(0);
	}

	/**
	 * extract optional sessionid parameter
	 */
	private static String extractConnectionUuid(Map<String, List<String>> requestParameterMap) {
		List<String> connectionUuids = requestParameterMap.get(CONNECTION_UUID_PARAM);
		return connectionUuids.isEmpty() ? "" : connectionUuids.get(0);
	}

	/**
	 * Unique key to temporarily store the zk session in the endpoint config userProperties.
	 * In cases where the endpoint config is shared a uuid can be provided to non-unique desktop Ids collisions
	 * across sessions, in highly concurrent usage scenarios.
	 */
	private static String tempSessionKey(String desktopId, String connectionUuid) {
		return desktopId + '|' + connectionUuid;
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
		String dtid = (String) wsession.getUserProperties().get(DESKTOP_ID);
		SessionCtrl sessionCtrl = (SessionCtrl) wsession.getUserProperties().get(ZK_SESSION);
		if (dtid == null || sessionCtrl == null) {
			throw new IllegalStateException("Destkop not initialized for Websocket session, "
					+ "call ZKWebSocket.initZkDesktop(Session, EndpointConfig) first: e.g. during onOpen");
		}
		DesktopCache desktopCache = sessionCtrl.getDesktopCache();
		return desktopCache != null ? desktopCache.getDesktopIfAny(dtid) : null;
	}
}
