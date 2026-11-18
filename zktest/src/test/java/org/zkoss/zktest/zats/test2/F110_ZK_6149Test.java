/* F110_ZK_6149Test.java

		Purpose:

		Description:

		History:
				Wed Nov 18 14:37:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6149Test extends WebDriverTestCase {

	// Chrome drops a disallowed unload listener without registering it, so record the calls themselves
	@Override
	protected WebDriver getWebDriver() {
		if (driver == null) {
			super.getWebDriver();
			((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of("source",
					"window.zk6149Listeners = [];"
					+ "var add = EventTarget.prototype.addEventListener;"
					+ "EventTarget.prototype.addEventListener = function (type) {"
					+ "if (this === window) window.zk6149Listeners.push(type);"
					+ "return add.apply(this, arguments);"
					+ "};"
					+ "var sendBeacon = Navigator.prototype.sendBeacon;"
					+ "Navigator.prototype.sendBeacon = function () {"
					+ "sessionStorage.setItem('zk6149Beacons', (sessionStorage.getItem('zk6149Beacons') || '') + zk.Desktop._dt.id + ';');"
					+ "return sendBeacon.apply(this, arguments);"
					+ "};"
					// neither ChromeDriver nor headless Chrome shows a leave prompt, so record what onbeforeunload asks
					+ "var bfu = Object.getOwnPropertyDescriptor(window, 'onbeforeunload');"
					+ "Object.defineProperty(window, 'onbeforeunload', {configurable: true, get: bfu.get, set: function (fn) {"
					+ "bfu.set.call(this, fn && function () {"
					+ "var s = fn.apply(this, arguments);"
					+ "if (s) sessionStorage.setItem('zk6149Asked', s);"
					+ "return s;"
					+ "});"
					+ "}});"));
		}
		return driver;
	}

	@Test
	public void noUnloadListener() {
		connect();
		List<String> types = Arrays.asList(getEval("zk6149Listeners.join()").split(","));
		assertFalse(types.contains("unload"), "registered window listeners: " + types);
		assertTrue(types.contains("pageshow"), "registered window listeners: " + types);
	}

	@Test
	public void backToRemovedDesktopReloads() {
		connect();
		String dtid = jq("$dtid").text();
		leaveAndComeBack();
		waitUntilReloaded(dtid);
		assertRemoved(dtid);
	}

	// removing an iframe fires pagehide in it but no beforeunload
	@Test
	public void removedIframeRemovesItsDesktop() {
		connect();
		eval("document.body.appendChild(Object.assign(document.createElement('iframe'), {id: 'zk6149frame', src: location.href}))");
		String frameDtid = "";
		for (int i = 0; i < 20 && frameDtid.isEmpty(); i++) {
			sleep(250);
			frameDtid = getEval("(function () { var w = document.getElementById('zk6149frame').contentWindow;"
					+ " return w.zk && !w.zk.mounting && w.zk.Desktop._dt ? w.zk.Desktop._dt.id : ''; })()");
		}
		assertFalse(frameDtid.isEmpty(), "the page in the iframe did not mount");
		eval("document.getElementById('zk6149frame').remove()");
		assertRemoved(frameDtid);
	}

	// zk.keepDesktop is what <?page cacheable="true"?> and keep-across-visits turn on
	@Test
	public void backToKeptDesktopResumes() {
		backToKeptDesktop("zk.keepDesktop = true");
	}

	// zk.pfmeter is what a performance meter turns on; rmDesktop then sets zk.rmDesktoping for a kept desktop too
	@Test
	public void backToKeptDesktopWithPerformanceMeterResumes() {
		backToKeptDesktop("zk.keepDesktop = zk.pfmeter = true");
	}

	// Executions.sendRedirect sets zAu.disabledRequest before the page leaves; Chrome keeps a page that sent
	// Cache-Control: no-store out of the back/forward cache, so the page is made cacheable for real
	@Test
	public void backToKeptDesktopAfterRedirectResumes() {
		connect();
		driver.get(driver.getCurrentUrl() + "?cacheable=1");
		waitResponse();
		String dtid = jq("$dtid").text();
		click(jq("$redirect"));
		for (int i = 0; i < 20 && !driver.getCurrentUrl().contains("zk6149=next"); i++)
			sleep(250);
		waitResponse();
		driver.navigate().back();
		waitResponse();
		click(jq("$btn"));
		waitResponse();
		assertEquals(dtid, jq("$dtid").text(), "the kept desktop should resume without a reload");
		assertFalse(jq("$removed").text().isEmpty(), "the resumed page did not reach the server");
	}

	// without a user gesture Chrome runs onbeforeunload, which returns before rmDesktop, but shows no dialog
	@Test
	public void confirmedLeaveRemovesDesktop() {
		connect();
		String dtid = jq("$dtid").text();
		eval("zk.confirmClose = 'leave?'");
		driver.get(driver.getCurrentUrl() + "?zk6149=next");
		waitResponse();
		assertRemoved(dtid);
		eval("sessionStorage.removeItem('zk6149Asked')");
		driver.navigate().back();
		waitUntilReloaded(dtid);
		assertEquals("null", getEval("String(sessionStorage.getItem('zk6149Asked'))"), "the reload asked to confirm the leave again");
	}

	// a leave that onbeforeunload does not finish, as on iOS, after a kept desktop resumed
	@Test
	public void confirmedLeaveAfterKeptDesktopResumesSendsPerformanceMeterBeacon() {
		connect();
		String dtid = jq("$dtid").text();
		eval("zk.keepDesktop = zk.pfmeter = true");
		leaveAndComeBack();
		waitResponse();
		eval("sessionStorage.removeItem('zk6149Beacons')");
		eval("zk.confirmClose = 'leave?'");
		driver.get(driver.getCurrentUrl() + "?zk6149=next");
		waitResponse();
		assertEquals(dtid + ";", getEval("sessionStorage.getItem('zk6149Beacons')"));
	}

	private void backToKeptDesktop(String flags) {
		connect();
		String dtid = jq("$dtid").text();
		eval(flags);
		leaveAndComeBack();
		waitResponse();
		assertEquals("false", getEval("zk.unloading"));
		click(jq("$btn"));
		waitResponse();
		assertEquals(dtid, jq("$dtid").text(), "the kept desktop should resume without a reload");
		String removed = jq("$removed").text();
		assertFalse(removed.isEmpty() || removed.contains(dtid), "removed desktops: " + removed);
	}

	private void leaveAndComeBack() {
		driver.get(driver.getCurrentUrl() + "?zk6149=next");
		waitResponse();
		driver.navigate().back();
	}

	private void waitUntilReloaded(String dtid) {
		for (int i = 0; i < 20; i++) {
			sleep(500);
			try {
				if (!dtid.equals(jq("$dtid").text()))
					break;
			} catch (RuntimeException navigating) {
				// the old document is gone and the new one is not ready yet
			}
		}
		waitResponse();
		assertNotEquals(dtid, jq("$dtid").text(), "the restored page should reload");
	}

	// the rmDesktop beacon is asynchronous, so the new page may load before it is handled
	private void assertRemoved(String dtid) {
		for (int i = 0; i < 10; i++) {
			click(jq("$btn"));
			waitResponse();
			if (jq("$removed").text().contains(dtid))
				return;
			sleep(500);
		}
		assertTrue(jq("$removed").text().contains(dtid), "desktop " + dtid + " was not removed");
	}
}
