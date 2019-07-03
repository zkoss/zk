/* F70_ZK_1919Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 18:07:57 CST 2019, Created by rudyhuang

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
public class F70_ZK_1919Test extends WebDriverTestCase {
	@BeforeClass
	public static void init() throws Exception {
		Library.setProperty("org.zkoss.zk.config.path", "/test2/F70-ZK-1919-zk.xml");

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

		String uuidPage = widget(jq("@page")).uuid();
		String uuidWindow = widget(jq("@window")).uuid();
		String uuidButton = widget(jq("@button")).uuid();
		String uuidLabel = widget(jq("@label")).uuid();

		driver.navigate().refresh();
		waitResponse();
		checkUuid(uuidPage, uuidWindow, uuidButton, uuidLabel);

		driver.navigate().refresh();
		waitResponse();
		checkUuid(uuidPage, uuidWindow, uuidButton, uuidLabel);
	}

	private void checkUuid(String uuidPage, String uuidWindow, String uuidButton, String uuidLabel) {
		Assert.assertEquals(uuidPage, widget(jq("@page")).uuid());
		Assert.assertEquals(uuidWindow, widget(jq("@window")).uuid());
		Assert.assertEquals(uuidButton, widget(jq("@button")).uuid());
		Assert.assertEquals(uuidLabel, widget(jq("@label")).uuid());
	}

	@AfterClass
	public static void afterClass() {
		Library.setProperty("org.zkoss.zk.config.path", null);
	}
}
