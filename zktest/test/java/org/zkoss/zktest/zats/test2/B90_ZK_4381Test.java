/* B90_ZK_4381Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 11 09:40:11 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.zats.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4381Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B86-ZK-4288-zk.xml");

	@BeforeClass
	public static void init() throws Exception {
		Server server = new Server(new InetSocketAddress(getHost(), 0));

		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		context.getSessionHandler().setSessionIdPathParameterName(null);
		Filter sessionRepositoryFilter = new SessionRepositoryFilter<>(new B86_ZK_4288SessionRepository());
		context.addFilter(new FilterHolder(sessionRepositoryFilter), "/*",  EnumSet.of(DispatcherType.REQUEST));
		server.setHandler(new HandlerList(context, new DefaultHandler()));
		initServer(server);
	}

	@Test
	public void test() {
		WebDriver driver = connect();
		waitResponse();

		driver.manage().window().setSize(new Dimension(1000, 768));
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
	}

	private static class B86_ZK_4288SessionRepository implements SessionRepository<Session> {
		private static final Logger LOG = LoggerFactory.getLogger(B86_ZK_4288SessionRepository.class);
		private final Map<String, byte[]> sessionDataCache = new ConcurrentHashMap<>();

		@Override
		public Session createSession() {
			return new MapSession();
		}

		@Override
		public void save(Session session) {
			LOG.debug("save: {}", session.getId());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				LOG.debug("writeObject: {}", session.getId());
				new ObjectOutputStream(baos).writeObject(session);
			} catch (Exception e) {
				LOG.error("", e);
			}
			sessionDataCache.put(session.getId(), baos.toByteArray());
		}

		@Override
		public Session findById(String id) {
			LOG.debug("findById: {}", id);
			byte[] bytes = sessionDataCache.get(id);
			if (bytes != null) {
				try {
					LOG.debug("readObject: {}", id);
					Session restoredSession = (Session) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
					MapSession newSession = new MapSession(id);
					for (String attributeName : restoredSession.getAttributeNames()) {
						newSession.setAttribute(attributeName, restoredSession.getAttribute(attributeName));
					}
					return newSession;
				} catch (Exception e) {
					LOG.error("", e);
				}
			}
			return null;
		}

		@Override
		public void deleteById(String id) {
			LOG.debug("deleteById: {}", id);
			sessionDataCache.remove(id);
		}
	}
}
