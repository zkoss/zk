/* B110_ZK_6090_TabboxMoldTest.java

	Purpose:

	Description:

	History:
		Tue Sep 09 10:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TabletWebDriverTestCase;

/**
 * The tablet augment of the tabbox narrows the mold down to the two it can draw. It used to
 * assign the narrowed mold without redrawing, so a mold pushed from the server left the DOM
 * belonging to the previous mold: only the accordion mold wraps a tabpanel's cave in a
 * -cave2, and getCaveNode() looks for exactly the node the mold says should be there.
 *
 * @author peakerlee
 */
@ForkJVMTestOnly
public class B110_ZK_6090_TabboxMoldTest extends TabletWebDriverTestCase {

	/** As drawn: the default mold, no -cave2, and a cave the panel can still find. */
	private static final String DEFAULT_DOM = "default,plain,no-cave2,cave";
	/** Redrawn in the accordion mold: the accordion class, a -cave2, and a cave again. */
	private static final String ACCORDION_DOM = "accordion,accordion,cave2,cave";

	/** A mold pushed from the server has to reach the DOM, not just the widget's field. */
	@Test
	public void testServerMoldPushRedraws() {
		connect();
		waitResponse();
		assertPreconditions();

		assertEquals(DEFAULT_DOM, getEval("window.zk6090Dom()"), "the page did not start in the default mold");

		click(jq("$toAccordion"));
		waitResponse();
		sleep(500); // the touch molds rebind their gestures 300ms after the redraw
		assertEquals(ACCORDION_DOM, getEval("window.zk6090Dom()"),
				"the pushed mold must be redrawn, not only assigned");
		assertNoError();

		click(jq("$toDefault"));
		waitResponse();
		sleep(500);
		assertEquals(DEFAULT_DOM, getEval("window.zk6090Dom()"), "switching back must redraw too");
		assertNoError();
	}

	/**
	 * accordion-lite is one of the molds the tablet augment maps onto accordion; the mapping
	 * still has to redraw, and the widget must end up reporting the mold it actually drew.
	 */
	@Test
	public void testMappedMoldPushRedraws() {
		connect();
		waitResponse();
		assertPreconditions();

		click(jq("$toAccordionLite"));
		waitResponse();
		sleep(500);
		assertEquals(ACCORDION_DOM, getEval("window.zk6090Dom()"),
				"accordion-lite must be mapped onto the accordion mold and redrawn");
		assertNoError();
	}

	/**
	 * Without the tablet UI the zkmax touch molds never load, the augment under test is not
	 * installed at all, and every assertion below would pass on the plain CE widget.
	 */
	private void assertPreconditions() {
		assertEquals("true", jq("$tabletUI").text(), "the tablet UI must be on");
		assertEquals("true", jq("$touch").text(), "touch must be on");
		// zk.Widget declares setMold, so only an own property proves the touch augment loaded
		assertEquals("true", getEval("Object.prototype.hasOwnProperty.call("
						+ "zul.tab.Tabbox.prototype, 'setMold')"),
				"the touch augment for zul.tab.Tabbox must be installed");
		assertEquals("true", getEval("'' + !!zul.tab.Tabbox.molds['accordion']"),
				"the accordion mold must be on the client for a mold push to redraw");
	}

	private void assertNoError() {
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "the mold push threw");
		assertNoJSError();
	}
}
