/* B80_ZK_3123Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Feb 15, 2016  3:09:44 PM, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.zats.test2;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.zkoss.zktest.zats.WebDriverTestCase;

import java.net.InetSocketAddress;

import static org.junit.Assert.fail;

/**
 * @author jameschu
 */
public class B80_ZK_3123Test {
	private static Server server;
	private WebDriver driver;
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

	protected WebDriver getWebDriver() {
		if (driver == null) {
			driver = new WebDriverTestCase.ZKWebDriver(true);
		}
		return driver;
	}

	@After
	public void stop() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

	@BeforeClass
	public static void init() throws Exception {
		server = new Server(new InetSocketAddress(getHost(), 0));
		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());

		Resource[] res = new Resource[] {Resource.newResource("./src/archive/"), JarResource.newJarResource(Resource.newResource("./src/archive/data/zk-3123-1.0.jar"))};
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

	@AfterClass
	public static void end() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

	@Test
	public void test() {
		WebDriverTestCase.ZKWebDriver webDriver = (WebDriverTestCase.ZKWebDriver)getWebDriver();
		webDriver.get("http://" + getHost() + ":" + getPort() + getContextPath() + "/index.zhtml");
		int errCode = webDriver.lastPage().getWebResponse().getStatusCode();
		if (errCode == 404) {
			fail("Error Code: " + errCode + ", from URL[" + webDriver.lastPage().getUrl() + "]");
		}
	}
}
