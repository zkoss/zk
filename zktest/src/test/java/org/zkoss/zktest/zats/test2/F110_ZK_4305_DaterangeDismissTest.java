/* F110_ZK_4305_DaterangeDismissTest.java

		Purpose:

		Description:

		History:
				Wed Oct  7 17:09:45 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class F110_ZK_4305_DaterangeDismissTest extends WebDriverTestCase {
	private boolean popupOpen() {
		return Boolean.parseBoolean(getEval("String(!!zk.Widget.$('$drb')._rangePopup._isOpen)"));
	}

	// The open popup is moved to the end of <body>, so find it by uuid, not under the box.
	private JQuery popup() {
		return jq("#" + getEval("zk.Widget.$('$drb')._rangePopup.uuid"));
	}

	private void openPopup() {
		connect("/test2/F110-ZK-4305-dismiss.zul");
		waitResponse();
		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertTrue(popupOpen(), "precondition: the popup is open");
	}

	@Test
	public void pressOutsideClosesOnlyOnRelease() {
		openPopup();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		waitResponse();
		assertTrue(popupOpen(), "a press outside must not close it");
		getActions().release().perform();
		waitResponse();
		assertFalse(popupOpen(), "releasing outside closes it");
	}

	@Test
	public void releaseBackOnThePopupKeepsItOpen() {
		openPopup();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(popup())).release().perform();
		waitResponse();
		assertTrue(popupOpen(), "a release back on the popup aborts the dismissal");
	}

	@Test
	public void releaseBackOnTheBoxKeepsItOpen() {
		openPopup();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq("$drb .z-daterangebox-begin"))).release().perform();
		waitResponse();
		assertTrue(popupOpen(), "a release back on the box aborts the dismissal");
	}

	// Focus moving to another field closes the popup through its focusin listener, not onFloatUp.
	@Test
	public void pressOnAnotherFieldClosesOnlyOnRelease() {
		openPopup();
		getActions().clickAndHold(toElement(jq("$tb"))).perform();
		waitResponse();
		assertTrue(popupOpen(), "a press that moves focus to another field must not close it");
		getActions().release().perform();
		waitResponse();
		assertFalse(popupOpen(), "releasing there closes it");
	}
}
