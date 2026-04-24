/* B110_ZK_6090_KnobSliderTest.java

	Purpose:

	Description:

	History:
		Fri Aug 21 11:40:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The knob mold of the slider adds a Draggable, two wheel handlers and, with za11y on,
 * two focus handlers. Unbinding has to take all of them off again, and so does switching
 * to another mold, which changes the mold before it redraws.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_KnobSliderTest extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	/**
	 * The handlers were registered with freshly bound functions, so taking them off with
	 * another freshly bound function matched nothing and left them on the node.
	 */
	@Test
	public void testUnbindReleasesHandlers() {
		connect();
		waitResponse();
		assertEquals("true", getEval("'' + !!window.zk6090.knob._knobDrag"),
				"the knob mold should have bound its Draggable");
		String bound = getEval("window.zk6090Handlers()");
		assertNotEquals("0,0,0,0", bound, "the knob mold should have bound its handlers");

		// the node stays in the page, the way a render-on-demand container recycles it
		getEval("(window.zk6090.knob.unbind(), 1)");
		assertEquals("0,0,0,0", getEval("window.zk6090Handlers()"),
				"unbinding must take every knob handler off, they were bound as " + bound);
		assertEquals("false", getEval("'' + !!window.zk6090.knob._knobDrag"),
				"unbinding must release the knob Draggable");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/**
	 * setMold() assigns the new mold and only then redraws, so a teardown that asks
	 * whether the mold is still "knob" never runs.
	 */
	@Test
	public void testMoldSwitchReleasesHandlers() {
		connect();
		waitResponse();
		assertEquals("true", getEval("'' + !!zul.inp.Slider.molds['default']"),
				"the plain mold must be on the client for the switch to redraw");
		assertEquals("true", getEval("'' + !!window.zk6090.knob._knobDrag"),
				"the knob mold should have bound its Draggable");

		getEval("(window.zk6090.knob.setMold('default'), 1)");
		sleep(500);
		assertEquals("default", getEval("window.zk6090.knob.getMold()"), "the mold switch did not take");
		assertEquals("false", getEval("'' + !!window.zk6090.knob._knobDrag"),
				"switching away from the knob mold must release the knob Draggable");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "the mold switch threw");
		assertNoJSError();
	}

	/**
	 * The same trap on the CE half: the plain mold's teardown was skipped whenever the
	 * mold read "knob" at unbind time, which is exactly what a switch <em>to</em> knob does.
	 */
	@Test
	public void testMoldSwitchToKnobReleasesHandlers() {
		connect();
		waitResponse();
		assertEquals("true", getEval("'' + !!window.zk6090.plain._drag"),
				"the plain mold should have bound its Draggable");

		getEval("(window.zk6090.plain.setMold('knob'), 1)");
		sleep(500);
		assertEquals("knob", getEval("window.zk6090.plain.getMold()"), "the mold switch did not take");
		assertEquals("false", getEval("'' + !!window.zk6090.plain._drag"),
				"switching to the knob mold must release the plain mold's Draggable");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "the mold switch threw");
		assertNoJSError();
	}
}
