/* B110_ZK_6090_DocListenerTest.java

	Purpose:

	Description:

	History:
		Fri Aug 21 14:30:00 CST 2026, Created by peakerlee

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

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A listener a widget puts on document or window outlives every node the widget owns, so it can
 * only go away if the widget's own teardown takes it off. Each case below unbinds one widget and
 * checks that exactly its own listener disappeared.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_DocListenerTest extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	/** The anchornav puts a scroll listener on window when it finds no explicit scroll target. */
	@Test
	public void testAnchornavWindowScroll() {
		connect();
		waitResponse();
		int before = count("window", "scroll");
		assertTrue(before > 0, "the anchornav should have put a scroll listener on window");

		getEval("(window.zk6090Unbind('an'), 1)");
		assertEquals(before - 1, count("window", "scroll"),
				"unbinding the anchornav must take its window scroll listener off");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** A collapsed region that has slid out listens on document for the click that closes it. */
	@Test
	public void testLayoutRegionDocumentClick() {
		connect();
		waitResponse();
		int base = count("document", "click");

		getEval("(window.zk6090.rgn.setSlide(true), 1)");
		sleep(1000); // the slide is animated, the listener goes on in afterSlideDown
		int slid = count("document", "click");
		assertEquals(base + 1, slid, "sliding the region out should add its document click listener");

		getEval("(window.zk6090Unbind('rgn'), 1)");
		assertEquals(base, count("document", "click"),
				"unbinding a region that is still slid out must take its document click listener off");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** detection="browser" makes the dropupload watch the whole document for a drag. */
	@Test
	public void testDropuploadDocumentDrag() {
		connect();
		waitResponse();
		int before = count("document", "dragenter");
		assertTrue(before > 0, "the dropupload should watch document for dragenter");

		getEval("(window.zk6090Unbind('du'), 1)");
		assertEquals(before - 1, count("document", "dragenter"),
				"unbinding the dropupload must take its document dragenter listener off");
		assertEquals(0, count("document", "dragleave"),
				"and its dragleave listener as well");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/**
	 * When a dedicated scroll target appears, its bind_ moves the anchornav off the window
	 * fallback and onto the target's own cave node. Unbinding the target has to undo both
	 * halves of that hand-off.
	 */
	@Test
	public void testScrollTargetHandsAnchornavBack() {
		connect();
		waitResponse();
		assertEquals(1, countCave("scroll"),
				"the scroll target should carry the anchornav's scroll listener");
		int windowBefore = count("window", "scroll");

		getEval("(window.zk6090Unbind('scroller'), 1)");
		assertEquals(0, countCave("scroll"),
				"unbinding the scroll target must take the anchornav's listener off its cave");
		assertEquals(windowBefore + 1, count("window", "scroll"),
				"and must hand the anchornav back to the window fallback its bind_ removed");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** The other direction: the target outlives the anchornav, so the anchornav must clean up. */
	@Test
	public void testAnchornavReleasesItsScrollTarget() {
		connect();
		waitResponse();
		assertEquals(1, countCave("scroll"),
				"the scroll target should carry the anchornav's scroll listener");

		getEval("(window.zk6090Unbind('an2'), 1)");
		assertEquals(0, countCave("scroll"),
				"unbinding the anchornav must take its listener off the scroll target it does not own");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	private int count(String target, String type) {
		return Integer.parseInt(getEval("'' + window.zk6090Count('" + target + "', '" + type + "')"));
	}

	private int countCave(String type) {
		return Integer.parseInt(
				getEval("'' + window.zk6090CountNode(window.zk6090.scrollerCave, '" + type + "')"));
	}
}
