/* B96_ZK_4194Test.java

	Purpose:
		
	Description:
		
	History:
		5:31 PM 2023/4/12, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Optional;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v111.network.Network;
import org.openqa.selenium.devtools.v111.network.model.ConnectionType;
import org.openqa.selenium.mobile.NetworkConnection;
import org.openqa.selenium.remote.CapabilityType;

import org.zkoss.zktest.zats.ChromiumHeadlessDriver;
import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
@Category(ForkJVMTestOnly.class)
public class B96_ZK_4194Test extends WebDriverTestCase {

	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B96_ZK_4194Test.class);

	protected boolean isUsingRemoteWebDriver(ChromeOptions driverOptions) {
		return false;
	}

	@Test
	public void testEventQueueNormalCase() {
		connect();
		ChromeDriver window1 = (ChromeDriver) driver;
		String currentUrl = driver.getCurrentUrl();

		ChromeDriver window2 = new ChromiumHeadlessDriver(getWebDriverOptions(), isHeadless());
		window2.get(currentUrl);
		_local.set(window2);
		waitResponse();

		click(jq("@button"));
		waitResponse();
		assertEquals(1, jq("$eventLog @label").length());

		_local.set(window1);
		assertEquals(1, jq("$eventLog @label").length());

		try (DevTools devTools = window1.getDevTools()) {
			devTools.createSession();
			// network offline
			devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
			devTools.send(Network.emulateNetworkConditions(true, 20, 20, 40, Optional.of(
					ConnectionType.WIFI)));

			_local.set(window2);
			for (int i = 0; i < 9; i++) {
				click(jq("@button"));
				waitResponse();
			}

			assertEquals(10, jq("$eventLog @label").length());

			_local.set(window1);
			assertEquals(1, jq("$eventLog @label").length());

		}

		click(jq("@button"));
		waitResponse();
		assertEquals(11, jq("$eventLog @label").length());

		window2.close();
	}

	@Test
	public void testEventQueueMaxSize() {
		connect();
		ChromeDriver window1 = (ChromeDriver) driver;
		String currentUrl = driver.getCurrentUrl();

		ChromeDriver window2 = new ChromiumHeadlessDriver(getWebDriverOptions(), isHeadless());
		window2.get(currentUrl);
		_local.set(window2);
		waitResponse();

		click(jq("@button"));
		waitResponse();
		assertEquals(1, jq("$eventLog @label").length());

		_local.set(window1);
		assertEquals(1, jq("$eventLog @label").length());

		try (DevTools devTools = window1.getDevTools()) {
			devTools.createSession();
			// network offline
			devTools.send(Network.enable(Optional.empty(), Optional.empty(),
					Optional.empty()));
			devTools.send(Network.emulateNetworkConditions(true, 20, 20, 40,
					Optional.of(ConnectionType.WIFI)));

			_local.set(window2);
			for (int i = 0; i < 19; i++) {
				click(jq("@button"));
				waitResponse();
			}

			assertEquals(20, jq("$eventLog @label").length());

			_local.set(window1);
			assertEquals(1, jq("$eventLog @label").length());

		}

		click(jq("@button"));
		waitResponse();
		assertEquals(17, jq("$eventLog @label").length());

		window2.close();
	}

	@Test
	public void testEventQueueTimeout() {
		connect();
		ChromeDriver window1 = (ChromeDriver) driver;
		String currentUrl = driver.getCurrentUrl();

		ChromeDriver window2 = new ChromiumHeadlessDriver(getWebDriverOptions(), isHeadless());
		window2.get(currentUrl);
		_local.set(window2);
		waitResponse();

		click(jq("@button"));
		waitResponse();
		assertEquals(1, jq("$eventLog @label").length());

		_local.set(window1);
		assertEquals(1, jq("$eventLog @label").length());

		try (DevTools devTools = window1.getDevTools()) {
			devTools.createSession();
			// network offline
			devTools.send(Network.enable(Optional.empty(), Optional.empty(),
					Optional.empty()));
			devTools.send(Network.emulateNetworkConditions(true, 20, 20, 40,
					Optional.of(ConnectionType.WIFI)));

			_local.set(window2);
			for (int i = 0; i < 19; i++) {
				click(jq("@button"));
				waitResponse();
			}

			assertEquals(20, jq("$eventLog @label").length());

			_local.set(window1);
			assertEquals(1, jq("$eventLog @label").length());

		}

		sleep(60_000); // wait for 1 minute to timeout

		click(jq("@button"));
		waitResponse();
		assertEquals(2, jq("$eventLog @label").length());

		window2.close();
	}
}