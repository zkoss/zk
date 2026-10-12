/* F110_ZK_6084_PopupDismissTest.java

		Purpose:

		Description:

		History:
				Tue Sep 29 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 2.5.2 for the Popup family: with za11y, a press outside a Popup, or outside a default
 * Notification (which an outside press removes for good), closes it only on release, and a
 * release back inside keeps it. Confirmpopup shares the same path, see
 * {@link F110_ZK_6084_ConfirmpopupDismissTest}.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_PopupDismissTest extends WebDriverTestCase {
	private static final String NOTIFICATION = ".z-notification";

	private void openPopup() {
		click(jq("$openPopup"));
		waitResponse();
	}

	private void openNotification() {
		click(jq("$notify"));
		waitResponse();
	}

	// ---- Popup ----

	@Test
	public void popupPressOutsideClosesOnlyOnRelease() {
		connect();
		openPopup();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		assertTrue(jq("$pp").isVisible(), "a press outside must not close the popup");
		getActions().release().perform();
		waitResponse();
		assertFalse(jq("$pp").isVisible(), "releasing outside closes it");
	}

	@Test
	public void popupReleaseBackInsideKeepsItOpen() {
		connect();
		openPopup();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq("$ppContent"))).release().perform();
		waitResponse();
		assertTrue(jq("$pp").isVisible(), "a release back inside aborts the dismissal");
	}

	// The press moves focus to the textbox, which fires a second onFloatUp from the focus change.
	@Test
	public void popupPressOnAFocusableTargetOutsideWaitsForRelease() {
		connect();
		openPopup();
		try {
			getActions().clickAndHold(toElement(jq("$outsideInput"))).perform();
			assertTrue(jq("$pp").isVisible(), "moving focus with the press must not close it either");
		} finally {
			getActions().release().perform();
		}
		waitResponse();
		assertFalse(jq("$pp").isVisible());
	}

	// An open popup is moved to the end of <body>, so a child popup's DOM is outside its parent's.
	@Test
	public void releaseInsideAChildPopupKeepsBothOpen() {
		connect("/test2/F110-ZK-6084-NestedPopupDismiss.zul");
		click(jq("$openOuter"));
		waitResponse();
		click(jq("$openInner"));
		waitResponse();
		assertTrue(jq("$outer").isVisible() && jq("$inner").isVisible(), "precondition: both popups are open");
		assertTrue(Boolean.parseBoolean(getEval("String(!zk.Widget.$('$outer').$n().contains(zk.Widget.$('$inner').$n()))")),
				"precondition: the child popup's DOM is outside its parent's");
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq("$innerContent"))).release().perform();
		waitResponse();
		assertTrue(jq("$inner").isVisible(), "the release landed in the child popup");
		assertTrue(jq("$outer").isVisible(), "a release inside a child popup is inside its parent too");
	}

	// ---- Notification ----

	@Test
	public void notificationPressOutsideDismissesOnlyOnRelease() {
		connect();
		openNotification();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		sleep(700); // the dismissal fades out for 500 ms before the notification is detached
		assertTrue(jq(NOTIFICATION).exists(), "a press outside must not remove the notification");
		getActions().release().perform();
		waitResponse();
		sleep(700);
		assertFalse(jq(NOTIFICATION).exists(), "releasing outside dismisses it");
	}

	@Test
	public void notificationReleaseBackOnItKeepsIt() {
		connect();
		openNotification();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq(NOTIFICATION))).release().perform();
		waitResponse();
		sleep(700);
		assertTrue(jq(NOTIFICATION).exists(), "a release back on the notification aborts the dismissal");
	}
}
