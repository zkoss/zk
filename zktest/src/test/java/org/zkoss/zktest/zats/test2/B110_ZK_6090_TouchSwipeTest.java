/* B110_ZK_6090_TouchSwipeTest.java

	Purpose:

	Description:

	History:
		Fri Aug 21 11:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TabletWebDriverTestCase;

/**
 * The zkmax touch molds give a calendar, a border layout region, a tab panel and a knob slider
 * their own zk.Swipe, and each of them used to look the node up again while unbinding, after
 * the widget had already lost it.
 *
 * @author peakerlee
 */
@ForkJVMTestOnly
public class B110_ZK_6090_TouchSwipeTest extends TabletWebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		// without the tablet UI the zkmax touch molds never load and this test would
		// exercise the plain CE hooks instead
		assertEquals("true", jq("$tabletUI").text());
		assertAugmented("zul.db.Calendar");
		assertAugmented("zul.layout.LayoutRegion");
		assertAugmented("zul.tab.Tabpanel");
		assertAugmented("zul.inp.Slider");

		sleep(1000); // the swipe is made 300ms after the widget is bound
		for (String name : new String[] { "cal", "rgn", "tp", "knob" }) {
			assertEquals("true", getEval("window.zk6090HasSwipe('" + name + "')"),
					name + " should have got its zk.Swipe, otherwise nothing is tested");
			getEval("(window.zk6090Unbind('" + name + "'), 1)");
			assertEquals("false", getEval("window.zk6090HasSwipe('" + name + "')"),
					name + " should have let its zk.Swipe go");
			assertEquals("false", getEval("window.zk6090StillListening('" + name + "')"),
					name + " should have taken the swipe listener off its node");
		}
		// "early" was unbound before its 300ms lazy bind fired, which then threw "Handle required"
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** zk.Widget already declares the swipe hooks, so only an own property proves the augment loaded. */
	private void assertAugmented(String widgetClass) {
		assertEquals("true", getEval("Object.prototype.hasOwnProperty.call("
						+ widgetClass + ".prototype, 'bindSwipe_')"),
				"the touch augment for " + widgetClass + " must be installed");
	}
}
