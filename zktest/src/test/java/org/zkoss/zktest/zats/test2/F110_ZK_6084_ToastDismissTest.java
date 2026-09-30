/* F110_ZK_6084_ToastDismissTest.java

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
 * WCAG 2.5.2 for Toast: an outside press removes a default toast for good, so with za11y it goes
 * only on the release, and a release back on the toast keeps it.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_ToastDismissTest extends WebDriverTestCase {
	private static final String TOAST = ".z-toast";

	private void showToast() {
		click(jq("$show"));
		waitResponse(true); // until the fade-in ends
	}

	@Test
	public void pressOutsideDismissesOnlyOnRelease() {
		connect();
		showToast();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		sleep(700); // a dismissal fades out for 500 ms before the toast is detached
		assertTrue(jq(TOAST).exists(), "a press outside must not dismiss the toast");
		getActions().release().perform();
		waitResponse();
		sleep(700);
		assertFalse(jq(TOAST).exists(), "releasing outside dismisses it");
	}

	@Test
	public void releaseBackOnTheToastKeepsIt() {
		connect();
		showToast();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq(TOAST))).release().perform();
		waitResponse();
		sleep(700);
		assertTrue(jq(TOAST).exists(), "a release back on the toast aborts the dismissal");
	}
}
