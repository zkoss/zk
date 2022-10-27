/* IRangesliderRichletTest.java

	Purpose:

	Description:

	History:
		Tue Mar 08 10:53:38 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.zpr.IRangeslider} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Rangeslider">Rangeslider</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.IRangeslider
 */
public class IRangesliderRichletTest extends WebDriverTestCase {
	@Test
	public void marks() {
		connect("/input/iRangeslider/marks");
		assertEquals("a", jq(".z-rangeslider-mark-label").text());
	}

	@Test
	public void markScale() {
		connect("/input/iRangeslider/markScale");
		assertEquals(3, jq(".z-rangeslider .z-rangeslider-mark-dot").length());
		click(jq("@button"));
		waitResponse();
		assertEquals(11, jq(".z-rangeslider .z-rangeslider-mark-dot").length());
	}

	@Test
	public void minMax() {
		connect("/input/iRangeslider/minMax");
		assertEquals("10", jq(".z-rangeslider-mark-label:first").text());
		assertEquals("90", jq(".z-rangeslider-mark-label:last").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("0", jq(".z-rangeslider-mark-label:first").text());
		assertEquals("100", jq(".z-rangeslider-mark-label:last").text());
	}

	@Test
	public void orient() {
		connect("/input/iRangeslider/orient");
		assertTrue(jq(".z-rangeslider-vertical").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-rangeslider-horizontal").exists());
	}

	@Test
	public void step() {
		connect("/input/iRangeslider/step");
		click(jq(".z-rangeslider-mark-dot:eq(2)"));
		waitResponse();
		assertEquals("50", jq(".z-sliderbuttons-tooltip:eq(1)").text());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-rangeslider-mark-dot:eq(3)"));
		waitResponse();
		assertEquals("60", jq(".z-sliderbuttons-tooltip:eq(1)").text());
	}

	@Test
	public void tooltipVisible() {
		connect("/input/iRangeslider/tooltipVisible");
		assertTrue(jq(".z-sliderbuttons-tooltip").isVisible());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-sliderbuttons-tooltip").isVisible());
	}

	@Test
	public void startEndValue() {
		connect("/input/iRangeslider/startEndValue");
		assertEquals("10", jq(".z-sliderbuttons-tooltip:eq(0)").text());
		assertEquals("20", jq(".z-sliderbuttons-tooltip:eq(1)").text());
	}
}