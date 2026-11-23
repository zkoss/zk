package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_5155Test extends WebDriverTestCase {
	Actions act = new Actions(connect());

	@Test
	public void test() {
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
		Assert.assertTrue(jq(".z-bandbox-open").exists());
	}

	void assertBandboxClose() {
		Assert.assertFalse(jq(".z-bandbox-open").exists());
	}
}
