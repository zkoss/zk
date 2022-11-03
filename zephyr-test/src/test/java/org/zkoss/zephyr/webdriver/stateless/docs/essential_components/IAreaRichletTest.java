/* IAreaRichletTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Feb 07 16:36:34 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IArea;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IArea} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Imagemap/Area">IArea</a>,
 * if any.
 * @author leon
 * @see IArea
 */
public class IAreaRichletTest extends WebDriverTestCase {

	@Test
	public void testCoords() {
		connect("/essential_components/iarea/coords");
		clickAt(jq(".z-imagemap>img:eq(0)"), -10, 0);
		waitResponse();
		assertEquals("left", getZKLog());
		click(jq("@button"));
		waitResponse();
		clickAt(jq(".z-imagemap>img:eq(0)"), -50, 0);
		waitResponse();
		assertEquals("left", getZKLog());
	}

	@Test
	public void testOverlap() {
		connect("/essential_components/iarea/overlap");
		clickAt(jq(".z-imagemap>img:eq(0)"), -10, 0);
		waitResponse();
		assertEquals("left", getZKLog());
		closeZKLog();
		waitResponse();
		clickAt(jq(".z-imagemap>img:eq(0)"), 10, 0);
		waitResponse();
		assertEquals("all", getZKLog());
	}

	@Test
	public void testShape() {
		connect("/essential_components/iarea/shape");
		clickAt(jq(".z-imagemap>img:eq(0)"), 5, 0);
		waitResponse();
		assertEquals("circle1", getZKLog());
		closeZKLog();
		waitResponse();
		clickAt(jq(".z-imagemap>img:eq(0)"), 15, 0);
		waitResponse();
		assertEquals("circle2", getZKLog());
		closeZKLog();
		waitResponse();
		clickAt(jq(".z-imagemap>img:eq(0)"), 25, 0);
		waitResponse();
		assertEquals("circle3", getZKLog());
		closeZKLog();
		click(jq("@button"));
		waitResponse();
		clickAt(jq(".z-imagemap>img:eq(0)"), 25, 0);
		assertEquals("null", getZKLog());
	}

	@Test
	public void testTabindex() {
		connect("/essential_components/iarea/tabindex");
		assertEquals("1", jq(".z-area:eq(0)").attr("tabindex"));
		assertEquals("0", jq(".z-area:eq(1)").attr("tabindex"));
		click(jq("@button"));
		waitResponse();
		assertEquals("0", jq(".z-area:eq(0)").attr("tabindex"));
		assertEquals("1", jq(".z-area:eq(1)").attr("tabindex"));
	}

	@Test
	public void testTooltiptext() {
		connect("/essential_components/iarea/tooltiptext");
		assertEquals("tooltip", jq(".z-area:eq(0)").attr("title"));
		click(jq("@button"));
		waitResponse();
		assertEquals("new tooltip", jq(".z-area:eq(0)").attr("title"));
	}
}
