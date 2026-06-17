/* B104_ZK_6042Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 15:58:20 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6042: on the tablet UI (iPad emulation), the panel maximize and close
 * icons were partially clipped because the header used overflow:hidden while
 * the tablet theme grew the icon font-size to 22px. The fix lets the head
 * grow vertically and lifts the icon's line-height so each icon renders
 * within its bounding box.
 */
@ForkJVMTestOnly
public class B104_ZK_6042Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation",
						Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void panelIconsRenderFullyOnTablet() {
		connect();
		waitResponse();

		// Sanity: ensure the tablet theme is actually active. Otherwise the
		// regular desktop styles might pass the geometry assertions below
		// for the wrong reason.
		assertEquals("22px", getComputedFontSize(".z-panel-maximize"),
				"tablet UI must be loaded — .z-panel-maximize should use the"
						+ " 22px tablet font-size. If this fails, the test is"
						+ " not actually exercising the bug.");

		// Each icon must paint with non-zero width AND its rendered height
		// must be at least the visible glyph (>= 22px tablet font-size).
		// Before the fix the head clipped the icon so its computed height
		// was the smaller @baseButtonHeight (~16px on the default theme).
		assertIconRenders(".z-panel-maximize");
		assertIconRenders(".z-panel-close");
	}

	private String getComputedFontSize(String selector) {
		return getEval("(function(){var n=document.querySelector('" + selector
				+ "');return n?getComputedStyle(n).fontSize:null;})()");
	}

	private void assertIconRenders(String selector) {
		assertTrue(jq(selector).exists(), selector + " must exist");
		int width = Integer.parseInt(getEval("Math.round(jq('" + selector
				+ "')[0].getBoundingClientRect().width)"));
		int height = Integer.parseInt(getEval("Math.round(jq('" + selector
				+ "')[0].getBoundingClientRect().height)"));
		assertTrue(width > 0, selector + " width must be > 0, got " + width);
		assertTrue(height >= 22,
				selector + " must be tall enough to show the 22px tablet icon"
						+ " (ZK-6042); got height=" + height);
	}
}
