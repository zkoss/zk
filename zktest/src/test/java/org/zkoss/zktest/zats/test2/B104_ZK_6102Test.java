/* B104_ZK_6102Test.java

	Purpose:

	Description:

	History:
		Tue Jun 23 10:58:43 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.SessionDesktopCacheProvider;
import org.zkoss.zk.ui.impl.SimpleDesktopCache;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.util.Configuration;

/**
 * ZK-6102: SimpleDesktopCache cleaner timers must not survive web app stop.
 */
public class B104_ZK_6102Test {

	@Test
	public void appStopShouldCancelCleanerTimersWithoutDestroyingDesktops() throws Exception {
		final SessionDesktopCacheProvider provider = new SessionDesktopCacheProvider();
		final WebApp wapp = stubWebApp(newConfiguration());
		provider.start(wapp);
		try {
			final Set<Thread> before = timerThreads();
			final SimpleDesktopCache cache = (SimpleDesktopCache) provider.getDesktopCache(stubSession(wapp));
			final Desktop desktop = newDesktop("zk6102-live");
			cache.addDesktop(desktop);
			final Set<Thread> created = createdTimerThreads(before);
			assertEquals(1, created.size(), "the session-scoped desktop cache should start one cleaner timer");

			provider.stop(wapp);

			assertAllDead(created, "cleaner timer leaked after webapp stop");
			assertSame(desktop, cache.getDesktopIfAny("zk6102-live"),
					"webapp stop must not destroy passivatable session desktops");
		} finally {
			provider.stop(wapp); // never leak the cleaner timer if an assertion above fails
		}
	}

	@Test
	public void passivateShouldCancelCleanerTimerAndActivateShouldRestoreIt() throws Exception {
		final SessionDesktopCacheProvider provider = new SessionDesktopCacheProvider();
		final WebApp wapp = stubWebApp(newConfiguration());
		provider.start(wapp);
		try {
			final Session sess = stubSession(wapp);
			Set<Thread> before = timerThreads();
			final SimpleDesktopCache cache = (SimpleDesktopCache) provider.getDesktopCache(sess);
			final Desktop desktop = newPassivatingDesktop("zk6102-passivated", false);
			cache.addDesktop(desktop);
			Set<Thread> created = createdTimerThreads(before);
			assertEquals(1, created.size());

			for (int cycle = 0; cycle < 2; cycle++) {
				provider.sessionWillPassivate(sess);
				assertAllDead(created, "cleaner timer leaked after session passivation");
				assertSame(desktop, cache.getDesktopIfAny("zk6102-passivated"),
						"session passivation must keep desktops for serialization");

				before = timerThreads();
				provider.sessionDidActivate(sess);
				created = createdTimerThreads(before);
				assertEquals(1, created.size(),
						"session activation should restore exactly one cleaner timer");
			}

			provider.stop(wapp);
			assertAllDead(created, "cleaner timer of a reactivated session leaked after webapp stop");

			before = timerThreads();
			provider.sessionDidActivate(sess);
			assertEquals(0, createdTimerThreads(before).size(),
					"a stopped provider must not re-arm a cleaner timer from a late activation callback");
		} finally {
			provider.stop(wapp); // never leak the cleaner timer if an assertion above fails
		}
	}

	@Test
	public void sessionDestroyedShouldCancelCleanerTimer() throws Exception {
		final SessionDesktopCacheProvider provider = new SessionDesktopCacheProvider();
		final WebApp wapp = stubWebApp(newConfiguration());
		provider.start(wapp);
		try {
			final Session sess = stubSession(wapp);
			final Set<Thread> before = timerThreads();
			provider.getDesktopCache(sess);
			final Set<Thread> created = createdTimerThreads(before);
			assertEquals(1, created.size());

			provider.sessionDestroyed(sess);
			assertAllDead(created, "cleaner timer leaked after session destroyed");
		} finally {
			provider.stop(wapp); // never leak the cleaner timer if an assertion above fails
		}
	}

	@Test
	public void cacheStopShouldCancelCleanerTimerWhenDesktopCleanupFails() throws Exception {
		final Set<Thread> before = timerThreads();
		final SimpleDesktopCache cache = new SimpleDesktopCache(newConfiguration());
		final Set<Thread> created = createdTimerThreads(before);
		assertFalse(created.isEmpty(), "SimpleDesktopCache should start a cleaner timer");

		cache.addDesktop(newFailingDesktop("zk6102-cleanup-fails"));
		assertThrows(RuntimeException.class, cache::stop);
		assertAllDead(created, "cleaner timer leaked when desktop cleanup failed");
	}

	@Test
	public void sessionWillPassivateShouldKeepCleanerWhenPassivationFails() throws Exception {
		final Set<Thread> before = timerThreads();
		final SimpleDesktopCache cache = new SimpleDesktopCache(newConfiguration());
		final Set<Thread> created = createdTimerThreads(before);
		assertFalse(created.isEmpty(), "SimpleDesktopCache should start a cleaner timer");

		cache.addDesktop(newPassivatingDesktop("zk6102-passivation-fails", true));
		try {
			assertThrows(RuntimeException.class,
					() -> cache.sessionWillPassivate(stubSession(stubWebApp(newConfiguration()))));
			for (Thread timer : created)
				assertTrue(timer.isAlive(),
						"cleaner timer should remain alive if passivation fails before the cache is abandoned");
		} finally {
			try {
				cache.stop();
			} catch (RuntimeException ignored) {
			}
			assertAllDead(created, "cleaner timer leaked during test cleanup");
		}
	}

	private static Configuration newConfiguration() {
		final Configuration config = new Configuration();
		config.setDesktopMaxInactiveInterval(600); // >= 0 starts a cleaner timer.
		return config;
	}

	private static WebApp stubWebApp(final Configuration config) {
		return (WebApp) Proxy.newProxyInstance(B104_ZK_6102Test.class.getClassLoader(),
				new Class[] { WebApp.class }, new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) {
						if ("getConfiguration".equals(method.getName()))
							return config;
						return defaultValue(method.getReturnType());
					}
				});
	}

	private static Session stubSession(final WebApp wapp) {
		final DesktopCache[] slot = new DesktopCache[1];
		return (Session) Proxy.newProxyInstance(B104_ZK_6102Test.class.getClassLoader(),
				new Class[] { Session.class, SessionCtrl.class }, new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) {
						final String name = method.getName();
						if ("getDesktopCache".equals(name))
							return slot[0];
						if ("setDesktopCache".equals(name)) {
							slot[0] = (DesktopCache) args[0];
							return null;
						}
						if ("getWebApp".equals(name))
							return wapp;
						return defaultValue(method.getReturnType());
					}
				});
	}

	private static Desktop newDesktop(String id) {
		return (Desktop) Proxy.newProxyInstance(B104_ZK_6102Test.class.getClassLoader(),
				new Class[] { Desktop.class }, (proxy, method, args) -> {
					if ("getId".equals(method.getName()))
						return id;
					return defaultValue(method.getReturnType());
				});
	}

	private static Desktop newFailingDesktop(String id) {
		return (Desktop) Proxy.newProxyInstance(B104_ZK_6102Test.class.getClassLoader(),
				new Class[] { Desktop.class }, (proxy, method, args) -> {
					if ("getId".equals(method.getName()))
						return id;
					if ("getSession".equals(method.getName()))
						throw new RuntimeException("forced cleanup failure");
					return defaultValue(method.getReturnType());
				});
	}

	private static Desktop newPassivatingDesktop(String id, boolean failPassivation) {
		return (Desktop) Proxy.newProxyInstance(B104_ZK_6102Test.class.getClassLoader(),
				new Class[] { Desktop.class, DesktopCtrl.class }, (proxy, method, args) -> {
					if ("getId".equals(method.getName()))
						return id;
					if ("sessionWillPassivate".equals(method.getName())) {
						if (failPassivation)
							throw new RuntimeException("forced passivation failure");
						return null;
					}
					return defaultValue(method.getReturnType());
				});
	}

	private static Set<Thread> createdTimerThreads(Set<Thread> before) {
		final Set<Thread> created = timerThreads();
		created.removeAll(before);
		return created;
	}

	private static Set<Thread> timerThreads() {
		final Set<Thread> threads = new HashSet<Thread>();
		for (Thread t : Thread.getAllStackTraces().keySet())
			if (t.isAlive() && t.getName().startsWith("Timer-"))
				threads.add(t);
		return threads;
	}

	private static void assertAllDead(Set<Thread> threads, String message) throws InterruptedException {
		for (Thread timer : threads)
			// Timer.cancel() ends the thread asynchronously; allow slack on slow CI.
			assertTrue(waitUntilThreadGone(timer, 10000), message + ": " + timer.getName());
	}

	private static boolean waitUntilThreadGone(Thread thread, long timeoutMs) throws InterruptedException {
		final long deadline = System.currentTimeMillis() + timeoutMs;
		while (System.currentTimeMillis() < deadline) {
			if (!thread.isAlive())
				return true;
			Thread.sleep(50);
		}
		return !thread.isAlive();
	}

	private static Object defaultValue(Class<?> type) {
		if (type == boolean.class)
			return Boolean.FALSE;
		if (type == int.class)
			return Integer.valueOf(0);
		if (type == long.class)
			return Long.valueOf(0L);
		if (type == double.class)
			return Double.valueOf(0);
		if (type == float.class)
			return Float.valueOf(0);
		if (type == short.class)
			return Short.valueOf((short) 0);
		if (type == byte.class)
			return Byte.valueOf((byte) 0);
		if (type == char.class)
			return Character.valueOf('\0');
		return null;
	}
}
