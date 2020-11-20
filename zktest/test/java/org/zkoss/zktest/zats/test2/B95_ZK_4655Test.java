/* B95_ZK_4655Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 20 16:26:14 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.zkoss.zktest.test2.B95_ZK_4655Endpoint;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4655Test extends WebDriverTestCase {
	@BeforeClass
	public static void init() throws Exception {
		Server server = new Server(new InetSocketAddress(getHost(), 0));

		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		context.getSessionHandler().setSessionIdPathParameterName(null);
		server.setHandler(new HandlerList(context, new DefaultHandler()));
		// Add javax.websocket support
		final ServerContainer container = WebSocketServerContainerInitializer.initialize(context);
		container.addEndpoint(B95_ZK_4655Endpoint.class);
		initServer(server);
	}

	@Test
	public void test() {
		connect();
		sleep(1000); // wait for WebSocket
		Assert.assertFalse(isZKLogAvailable());
		assertNoJSError();
	}
}
