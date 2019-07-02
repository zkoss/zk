/* B70_ZK_2397Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 01 16:27:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.zkoss.lang.Library;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2397Test extends WebDriverTestCase {
	@BeforeClass
	public static void init() throws Exception {
		Library.setProperty("org.zkoss.zk.config.path", "/test2/B70-ZK-2397-zk.xml");

		Server server = new Server(new InetSocketAddress(getHost(), 0));

		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		context.getSessionHandler().setSessionIdPathParameterName(null);
		server.setHandler(new HandlerList(context, new DefaultHandler()));
		initServer(server);
	}

	@Test
	public void test() {
		connect();
		waitResponse();
		sleep(1000);

		click(jq("@textbox"));
		waitResponse();

		Assert.assertTrue(hasError());
		Assert.assertEquals("Session timeout. Please reload.", getMessageBoxContent());

		click(jq("@button:contains(Reload)"));
		sleep(100);
		Assert.assertFalse(hasError());
	}

	@AfterClass
	public static void afterClass() {
		Library.setProperty("org.zkoss.zk.config.path", null);
	}
}
