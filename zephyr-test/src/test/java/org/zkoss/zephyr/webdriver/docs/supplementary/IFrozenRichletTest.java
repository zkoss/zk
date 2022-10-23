/* IFrozenRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 12:09:24 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.supplementary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IFrozen} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Frozen">Frozen</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IFrozen
 */
public class IFrozenRichletTest extends WebDriverTestCase {
	@Test
	public void columns() {
		connect("/supplementary/iFrozen/columns");
		assertTrue(jq(".z-column:eq(0)").hasClass("z-frozen-col"));
		assertTrue(jq(".z-column:eq(1)").hasClass("z-frozen-col"));
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-column:eq(1)").hasClass("z-frozen-col"));
	}

	@Test
	public void rightColumns() {
		connect("/supplementary/iFrozen/rightColumns");
		assertTrue(jq(".z-column:eq(1)").hasClass("z-frozen-right-col"));
		assertTrue(jq(".z-column:eq(2)").hasClass("z-frozen-right-col"));
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-column:eq(1)").hasClass("z-frozen-right-col"));
	}

	@Test
	public void smooth() {
		connect("/supplementary/iFrozen/smooth");
		String script = "zk.$('$frozen')._smooth";
		assertEquals("false", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("true", WebDriverTestCase.getEval(script));
	}

	// Todo
	//ZK-5146: Frozen start doesn't work
//	@Test
//	public void start() {
//		connect("/supplementary/iFrozen/start");
//	}
}