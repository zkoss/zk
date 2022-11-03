/* IRowRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 01 16:21:20 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IRow;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IRow} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/data/Row">Row</a>,
 * if any.
 *
 * @author katherine
 * @see IRow
 */
public class IRowRichletTest extends WebDriverTestCase {
	@Test
	public void align() {
		connect("/data/iRow/align");
		assertTrue(jq(".z-row").attr("style").contains("text-align:center"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-row").attr("style").contains("text-align: left"));
	}

	@Test
	public void nowrap() {
		connect("/data/iRow/nowrap");
		assertEquals("nowrap", jq(".z-row-inner").attr("nowrap"));
		click(jq("@button"));
		waitResponse();
		assertEquals("null", jq(".z-row-inner").attr("nowrap"));
	}

	@Test
	public void valign() {
		connect("/data/iRow/valign");
		assertTrue(jq(".z-row").attr("style").contains("vertical-align:bottom"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-row").attr("style").contains("vertical-align: top"));
	}
}