/* B110_ZK_6090_NavTest.java

	Purpose:

	Description:

	History:
		Thu Aug 21 10:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A collapsed nav pops out by moving its -text and -cave nodes to document.body, and puts them
 * back from a timer 100ms after the mouse leaves. A nav that is unbound inside that window has to
 * take the timer with it and put the nodes back itself, otherwise they are stranded in the page.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_NavTest extends WebDriverTestCase {
	/** the delayed close is 100ms away; a third of a second is a wide margin */
	private static final long SETTLE_MS = 300;

	@Test
	public void test() {
		connect();
		waitResponse();

		assertEquals("true", getEval("zk6090NavOpen()"),
				"hovering a topmost nav of a collapsed navbar should pop it out");
		assertEquals("true", getEval("zk6090NavAtBody()"),
				"the popped out -text should have been moved to document.body");

		// leaves the nav and drops it in the same task, so unbind_ runs while the delayed
		// close is still pending
		getEval("(zk6090NavLeaveAndDrop(), 1)");
		sleep(SETTLE_MS);
		// ZK-6090: _doMouseLeave had already cleared _shallPopup, so unbind_ skipped its
		// teardown and left both nodes behind in document.body
		assertEquals("", getEval("zk6090NavStrays()"),
				"dropping the nav should take the popped out nodes with it");

		// the control still pops out and closes the ordinary way
		assertEquals("true", getEval("zk6090NavCtrlOpen()"),
				"the control nav should pop out");
		assertEquals("true,true", getEval("zk6090NavCtrlPopup()"),
				"the control popup should sit in document.body while it is open");
		getEval("(zk6090NavCtrlLeave(), 1)");
		sleep(SETTLE_MS);
		assertEquals("false,false", getEval("zk6090NavCtrlPopup()"),
				"the control popup should close by itself once the mouse has left");
		assertNoAnyError();
	}
}
