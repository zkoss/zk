/* B110_ZK_6090_TouchSwipeTest.java

	Purpose:

	Description:

	History:
		Fri Aug 21 11:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TabletWebDriverTestCase;

/**
 * The zkmax touch molds give a calendar, a border layout region and a tab panel their own
 * zk.Swipe, and each of them used to look the node up again while unbinding, after the
 * widget had already lost it.
 *
 * @author peakerlee
 */
@ForkJVMTestOnly
public class B110_ZK_6090_TouchSwipeTest extends TabletWebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

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

		sleep(1000); // the swipe is made 300ms after the widget is bound
		for (String name : new String[] { "cal", "rgn", "tp" }) {
			assertEquals("true", getEval("window.zk6090HasSwipe('" + name + "')"),
					name + " should have got its zk.Swipe, otherwise nothing is tested");
			getEval("(window.zk6090Unbind('" + name + "'), 1)");
			assertEquals("false", getEval("window.zk6090HasSwipe('" + name + "')"),
					name + " should have let its zk.Swipe go");
		}
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** The tablet mold of the chosenbox watches window for resize while it is bound. */
	@Test
	public void testChosenboxWindowResize() {
		connect();
		waitResponse();
		assertEquals("true", jq("$tabletUI").text());
		int before = Integer.parseInt(getEval("window.zk6090WindowResize()"));
		assertTrue(before > 0, "the tablet chosenbox should watch window for resize");

		getEval("(window.zk6090UnbindCbx(), 1)");
		assertEquals(before - 1, Integer.parseInt(getEval("window.zk6090WindowResize()")),
				"unbinding the chosenbox must take its window resize listener off");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** zk.Widget already declares the swipe hooks, so only an own property proves the augment loaded. */
	private void assertAugmented(String widgetClass) {
		assertEquals("true", getEval("Object.prototype.hasOwnProperty.call("
						+ widgetClass + ".prototype, 'unbindSwipe_')"),
				"the touch augment for " + widgetClass + " must be installed");
	}
}
