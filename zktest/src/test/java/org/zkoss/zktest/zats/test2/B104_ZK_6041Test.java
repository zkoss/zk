/* B104_ZK_6041Test.java

		Purpose:

		Description:

		History:
				Wed May 13 22:03:32 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_6041Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		// Click the searchbox to open the dropdown
		JQuery searchbox = jq(".z-searchbox");
		assertTrue(searchbox.exists(), "Searchbox should exist");

		click(searchbox);
		waitResponse();

		// Select the first item
		JQuery items = jq(".z-searchbox-item");
		assertTrue(items.exists(), "Searchbox items should exist");
		click(items.eq(0));
		waitResponse();

		// Verify the clear button is visible after selection
		JQuery clearBtn = jq(".z-searchbox-clear");
		assertTrue(clearBtn.exists(), "Clear button should exist");
		assertTrue(clearBtn.isVisible(), "Clear button should be visible after selection");

		// Verify the clear button is positioned inside the searchbox bounds:
		// The clear button's right edge should not exceed the searchbox's right edge.
		Boolean isInsideBounds = (Boolean) ((JavascriptExecutor) getWebDriver()).executeScript(
				"var sb = document.querySelector('.z-searchbox');"
				+ "var clear = document.querySelector('.z-searchbox-clear');"
				+ "if (!sb || !clear) return false;"
				+ "var sbRect = sb.getBoundingClientRect();"
				+ "var clearRect = clear.getBoundingClientRect();"
				// 1px tolerance: getBoundingClientRect() returns fractional values, so
				// border/rounding can push an in-bounds edge a fraction past the boundary.
				+ "return clearRect.right <= sbRect.right + 1 && clearRect.left >= sbRect.left - 1;");
		assertTrue(Boolean.TRUE.equals(isInsideBounds),
				"Clear button should be positioned inside the searchbox boundaries");
	}
}
