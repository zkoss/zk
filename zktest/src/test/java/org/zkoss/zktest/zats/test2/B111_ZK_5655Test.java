/* B111_ZK_5655Test.java

		Purpose:

		Description:

		History:
				Thu Nov 12 14:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B111_ZK_5655Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad Mini"));
	}

	@Test
	public void test() {
		connect();

		click(jq("$menu"));
		waitResponse();
		JQuery palette = jq(".z-colorpalette");
		JQuery picker = jq(".z-colorpicker");
		assertTrue(palette.isVisible());

		tap("picker-btn");
		assertTrue(picker.isVisible());
		assertFalse(palette.isVisible());

		tap("palette-btn");
		assertTrue(palette.isVisible());
		assertFalse(picker.isVisible());

		JQuery color = jq(".z-colorpalette-color").eq(10);
		String hex = color.attr("data-color");
		click(color);
		waitResponse();
		assertEquals(hex, jq("$lb").text());
	}

	// click() also fires click, which iOS Safari skips on these icons
	private void tap(String btn) {
		eval("(function () {"
				+ " var n = zk.$('$menu').$n('" + btn + "'), r = n.getBoundingClientRect(),"
				+ " t = new Touch({identifier: 1, target: n, clientX: r.left + r.width / 2, clientY: r.top + r.height / 2});"
				+ " ['touchstart', 'touchend'].forEach(function (type) {"
				+ " n.dispatchEvent(new TouchEvent(type, {bubbles: true, cancelable: true,"
				+ " touches: type == 'touchstart' ? [t] : [], changedTouches: [t]}));"
				+ " });"
				+ " })()");
	}
}
