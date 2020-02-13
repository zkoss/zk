/* B90_ZK_4441Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Feb 10 14:33:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.lang.Library;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4495Test extends WebDriverTestCase {
	@BeforeClass
	public static void init() throws Exception {
		Library.setProperty("org.zkoss.zk.config.path", "/test2/B90-ZK-4495.xml");
		Server server = new Server(new InetSocketAddress(getHost(), 0));

		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		context.getSessionHandler().setSessionIdPathParameterName(null);
		server.setHandler(new HandlerList(context, new DefaultHandler()));
		initServer(server);
	}

	@Override
	protected String getFileExtension() {
		return ".html";
	}
	@Test
	public void test() {
		WebDriver wd = connect();
		wd.findElement(By.id("btn")).click();
		new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.className("z-page")));
		assertEquals("aaa", wd.findElement(By.className("z-label")).getText());
	}

	@AfterClass
	public static void afterClass() {
		Library.setProperty("org.zkoss.zk.config.path", null);
	}
}
