/* B95_ZK_4655Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 20 16:26:14 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.junit.Test;

import org.zkoss.zk.ui.http.ZKWebSocket;

/**
 * @author rudyhuang
 */
public class B95_ZK_4655Test {
	private final Session sessionMock = mock(Session.class);
	private final EndpointConfig endpointConfigMock = mock(EndpointConfig.class);

	@Test
	public void testFullArgs() {
		List<String> dtid = Collections.singletonList("abcd");
		List<String> uuid = Collections.singletonList("1234");
		Map<String, List<String>> reqMap = new HashMap<>(2);
		reqMap.put(ZKWebSocket.DESKTOP_ID_PARAM, dtid);
		reqMap.put(ZKWebSocket.CONNECTION_UUID_PARAM, uuid);
		when(sessionMock.getRequestParameterMap()).thenReturn(reqMap);
		when(sessionMock.getUserPrincipal()).thenReturn(new ZKWebSocket.ZKPrinciple(null, uuid.get(0)));

		Map<String, Object> userParameters = new HashMap<>(1);
		userParameters.put(dtid.get(0) + '|' + uuid.get(0), mock(org.zkoss.zk.ui.Session.class));
		when(endpointConfigMock.getUserProperties()).thenReturn(userParameters);

		ZKWebSocket.initZkDesktop(sessionMock, endpointConfigMock);
	}

	@Test
	public void testArgOnlyDtid() {
		List<String> dtid = Collections.singletonList("abcd");
		when(sessionMock.getRequestParameterMap())
			.thenReturn(Collections.singletonMap(ZKWebSocket.DESKTOP_ID_PARAM, dtid));

		Map<String, Object> userParameters = new HashMap<>(1);
		userParameters.put(dtid.get(0) + '|', mock(org.zkoss.zk.ui.Session.class));
		when(endpointConfigMock.getUserProperties()).thenReturn(userParameters);

		ZKWebSocket.initZkDesktop(sessionMock, endpointConfigMock);
	}

	@Test(expected = IllegalStateException.class)
	public void testNoArg() {
		when(sessionMock.getRequestParameterMap()).thenReturn(Collections.emptyMap());

		ZKWebSocket.initZkDesktop(sessionMock, endpointConfigMock);
	}
}
