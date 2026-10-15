/* B110_ZK_5671Test.java

		Purpose:

		Description:

		History:
				Thu May 14 10:46:11 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B110_ZK_5671Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Widget pdfviewer = widget("@pdfviewer");
		Element canvas = pdfviewer.$n("content");
		JQuery page = jq(pdfviewer.$n("page"));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(d -> !pdfviewer.$n("page").get("style.width").isEmpty());
		String width = canvas.get("width");

		// browser zoom 150% after load: the 1920x1080 window at devicePixelRatio 1.5
		((ChromeDriver) driver).executeCdpCommand("Emulation.setDeviceMetricsOverride",
				Map.of("width", 1280, "height", 720, "deviceScaleFactor", 1.5, "mobile", false));
		wait.until(d -> !width.equals(canvas.get("width")));
		assertEquals(page.width(), jq(canvas).width(), 1);
		assertEquals(page.width() * 1.5, parseInt(canvas.get("width")), 2);
		// the page is painted across the whole backing store, not only its top-left 1/1.5
		wait.until(d -> Boolean.parseBoolean(getEval("(function (c) {"
				+ " var x = Math.ceil(c.width / 1.5), p = c.getContext('2d').getImageData(x, 0, c.width - x, c.height).data;"
				+ " for (var i = 0; i < p.length; i += 4) if (p[i + 3] && p[i] < 128) return true;"
				+ " return false; })(" + canvas + ")")));

		new Select(toElement(jq(pdfviewer.$n("toolbar-zoom")))).selectByValue("4");
		waitResponse();
		wait.until(d -> page.width() > 3000);
		assertEquals(page.width(), jq(canvas).width(), 1);
		assertTrue(parseInt(canvas.get("width")) > page.width());
		assertTrue((long) parseInt(canvas.get("width")) * parseInt(canvas.get("height")) <= 4096 * 4096);
	}
}
