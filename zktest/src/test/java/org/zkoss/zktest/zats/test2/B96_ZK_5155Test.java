package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class B96_ZK_5155Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		// 1. Click the magnifier glass. A popup should appear.
		openBandbox();
		assertBandboxOpen();

		// 2. Click the "next page" button in the popup. The popup shouldn't close.
		clickOn(".z-paging-button.z-paging-next");
		assertBandboxOpen();

		// 3. Click the "previous page" button in the popup. The popup shouldn't close.
		clickOn(".z-paging-button.z-paging-previous");
		assertBandboxOpen();

		// 4. Click the "click me for bandpopup to lose focus" button. The popup should close.
		clickOn("$outerButton");
		assertBandboxClose();
	}

	void openBandbox() {
		click(jq(".z-bandbox-button"));
		waitResponse();
	}

	void clickOn(String selector) {
		click(jq(selector));
		waitResponse();
	}

	void assertBandboxOpen() {
		assertTrue(jq(".z-bandbox-open").exists());
	}

	void assertBandboxClose() {
		assertFalse(jq(".z-bandbox-open").exists());
	}
}
