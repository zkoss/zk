/* F110_ZK_6085_PortallayoutDismissTest.java

	Purpose:

	Description:

	History:
		Fri Oct 16 10:53:34 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Portallayout's move popup: a click on the portallayout's own area, outside the popup, closes
 * it, and a press on the popup's padding focuses the popup itself, where its keys still work.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6085_PortallayoutDismissTest extends WebDriverTestCase {
	private static final String POPUP = ".z-portallayout-popup-open";

	private void open() {
		sendKeys(jq("$mail"), Keys.SPACE);
		waitResponse();
		Assertions.assertTrue(jq(POPUP).exists(), "precondition: Space opens the move popup");
	}

	private void clickPadding() {
		JQuery popup = jq(POPUP);
		// the middle of the popup's left padding
		int padding = (popup.innerWidth() - popup.width()) / 2;
		getActions().moveToElement(toElement(popup)).moveByOffset(padding / 2 - popup.innerWidth() / 2, 0).click().perform();
		waitResponse();
	}

	@Test
	public void clickOnThePortallayoutClosesThePopup() {
		connect();
		open();
		// a column width past the right column's centre: on the portallayout itself, beside its columns
		JQuery right = jq("$right");
		getActions().moveToElement(toElement(right)).moveByOffset(right.outerWidth(), 0).click().perform();
		waitResponse();
		Assertions.assertFalse(jq(POPUP).exists(), "a click on the portallayout outside the popup closes it");
	}

	@Test
	public void pressOnThePopupPaddingFocusesThePopup() {
		connect();
		open();
		clickPadding();
		Assertions.assertTrue(jq(POPUP).exists(), "a press on the popup does not close it");
		Assertions.assertTrue(jq(POPUP).is(":focus"), "the press focuses the popup itself");
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-portallayout-popup-nav").first().is(":focus"), "Down from the popup focuses the first item");
		clickPadding();
		getActions().sendKeys(Keys.ARROW_UP).perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-portallayout-popup-nav").last().is(":focus"), "Up from the popup focuses the last item");
		clickPadding();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assertions.assertTrue(jq(POPUP).exists(), "Enter on the popup itself does nothing");
		assertNoAnyError();
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		Assertions.assertFalse(jq(POPUP).exists(), "Escape still closes the popup");
		Assertions.assertTrue(jq("$mail").is(":focus"), "and returns the focus to the panel");
	}
}
