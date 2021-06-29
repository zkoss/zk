/* B96_ZK_4827Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 19 10:30:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.BeforeClass;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */

public class B96_ZK_4827Test extends WebDriverTestCase {
	@BeforeClass
	public static void init() throws Exception {
		Server server = new Server(new InetSocketAddress(getHost(), 0));

		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		context.getSessionHandler().setSessionIdPathParameterName(null);
		server.setHandler(new HandlerList(context, new DefaultHandler()));
		initServer(server);
	}

	@Test
	public void test() throws Exception {
		connect("/test2/B96-ZK-4827.jsp");
		sleep(3000);
		assertNoJSError();
	}
}
