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
 * @author jameschu
 */
public class B30_1753712Test extends WebDriverTestCase {
	@BeforeClass
	public static void init() throws Exception {
		Library.setProperty("org.zkoss.zk.config.path", "/test2/B30-1753712-zk.xml");
		Server server = new Server(new InetSocketAddress(getHost(), 0));
		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		server.setHandler(new HandlerList(context, new DefaultHandler()));
		initServer(server);
	}

	@AfterClass
	public static void cleanUp() {
		Library.setProperty("org.zkoss.zk.config.path", null);
	}

	@Test
	public void test() {
		connect();
		Assert.assertEquals("n/a", jq("$la").text());
		driver.navigate().refresh(); // trigger rmDesktop
		sleep(1000);
		waitResponse();
		Assert.assertNotEquals("n/a", jq("$la").text());
	}
}
