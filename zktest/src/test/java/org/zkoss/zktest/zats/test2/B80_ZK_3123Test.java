/* B80_ZK_3123Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Feb 15, 2016  3:09:44 PM, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;
import static org.zkoss.test.webdriver.WebDriverTestCase.getStatusCode;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author jameschu
 */
public class B80_ZK_3123Test {
	private static Server server;
	private static int port;
	private static int getPort() {
		return port;
	}
	private static String getContextPath() {
		return "/zktest";
	}
	private static String getHost() {
		return "127.0.0.1";
	}

	@BeforeAll
	public static void init() throws Exception {
		server = new Server(new InetSocketAddress(getHost(), 0));
		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());

		Resource[] res = new Resource[] {Resource.newResource("./src/main/webapp/"), JarResource.newJarResource(Resource.newResource("./src/main/webapp/data/zk-3123-1.0.jar"))};
		context.setBaseResource(new ResourceCollection(res));
		server.setHandler(context);
		server.start();

		for (Connector c : server.getConnectors()) {
			if (c instanceof NetworkConnector) {
				if (((NetworkConnector)c).getLocalPort() > 0) {
					port = ((NetworkConnector)c).getLocalPort();
					break;
				}
			}
		}
	}

	@AfterAll
	public static void end() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

	@Test
	public void test() {
		String url = "http://" + getHost() + ":" + getPort() + getContextPath() + "/index.zhtml";
		int errCode = getStatusCode(url);
		if (errCode == 404) {
			fail("Error Code: " + errCode + ", from URL[" + url + "]");
		}
	}
}
