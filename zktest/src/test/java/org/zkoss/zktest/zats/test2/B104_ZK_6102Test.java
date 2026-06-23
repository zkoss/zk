/* B104_ZK_6102Test.java

		Purpose:

		Description:

		History:
				Tue Jun 23 10:58:43 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.SessionDesktopCacheProvider;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Verifies that {@link SessionDesktopCacheProvider#stop} cancels the cleaner
 * timer of every per-session cache it created, so no {@code Timer-N} thread
 * survives a web app undeploy (the symptom Tomcat reports as a leaked thread).
 * The embedded Jetty runs in the test JVM, so the provider's timer threads are
 * observable here directly.
 */
public class B104_ZK_6102Test extends WebDriverTestCase {

	@Test
	public void testProviderStopCancelsCleanerTimer() throws Exception {
		// A default Configuration has desktop-timeout = 3600 (>= 0), so each
		// SimpleDesktopCache the provider creates starts a cleaner Timer.
		final Configuration config = new Configuration();
		final WebApp wapp = (WebApp) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { WebApp.class }, (proxy, method, args) ->
						"getConfiguration".equals(method.getName()) ? config : defaultValue(method));

		final SessionDesktopCacheProvider provider = new SessionDesktopCacheProvider();
		provider.start(wapp);

		final Set<String> before = timerThreadNames();
		provider.getDesktopCache(newStubSession()); // creates a cache + cleaner Timer
		final Set<String> created = timerThreadNames();
		created.removeAll(before);
		assertFalse(created.isEmpty(), "getDesktopCache should start a cleaner Timer");

		provider.stop(wapp); // ZK-6102: must cancel the cleaner Timer(s) it started
		for (String timerName : created)
			assertTrue(waitUntilThreadGone(timerName, 5000),
					"cleaner Timer thread [" + timerName + "] must be cancelled by provider.stop()");
	}

	/** A Session/SessionCtrl stub whose desktop-cache slot starts empty, so the
	 * provider creates and registers a fresh cache for it. */
	private static Session newStubSession() {
		final DesktopCache[] slot = new DesktopCache[1];
		return (Session) Proxy.newProxyInstance(B104_ZK_6102Test.class.getClassLoader(),
				new Class[] { Session.class, SessionCtrl.class }, new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) {
						switch (method.getName()) {
						case "getDesktopCache":
							return slot[0];
						case "setDesktopCache":
							slot[0] = (DesktopCache) args[0];
							return null;
						default:
							return defaultValue(method);
						}
					}
				});
	}

	private static Object defaultValue(Method method) {
		final Class<?> rt = method.getReturnType();
		if (rt == boolean.class)
			return false;
		if (rt.isPrimitive() && rt != void.class)
			return 0;
		return null;
	}

	private static Set<String> timerThreadNames() {
		final Set<String> names = new HashSet<>();
		for (Thread t : Thread.getAllStackTraces().keySet())
			if (t.isAlive() && t.getName().startsWith("Timer-"))
				names.add(t.getName());
		return names;
	}

	private static boolean waitUntilThreadGone(String name, long timeoutMs) throws InterruptedException {
		final long deadline = System.currentTimeMillis() + timeoutMs;
		while (System.currentTimeMillis() < deadline) {
			if (!timerThreadNames().contains(name))
				return true;
			Thread.sleep(50);
		}
		return !timerThreadNames().contains(name);
	}
}
