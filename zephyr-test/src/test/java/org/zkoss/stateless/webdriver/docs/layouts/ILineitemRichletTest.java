/* ILineitemRichletTest.java

	Purpose:

	Description:

	History:
		Thu Apr 07 12:30:00 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.ILineitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Lineitem">Lineitem</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ILineitem
 */
public class ILineitemRichletTest extends WebDriverTestCase {
	@Test
	public void backSpace() {
		connect("/layouts/iLineitem/backSpace");
		assertTrue(jq(".z-lineitem").attr("style").contains("margin: 0px 0px 10px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-lineitem").attr("style").contains("margin: 0px 0px 20px"));
	}

	@Test
	public void frontSpace() {
		connect("/layouts/iLineitem/frontSpace");
		assertTrue(jq(".z-lineitem").attr("style").contains("margin: 10px 0px 0px"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-lineitem").attr("style").contains("margin: 20px 0px 0px"));
	}

	@Test
	public void opposite() {
		connect("/layouts/iLineitem/opposite");
		assertTrue(jq(".z-lineitem").children().hasClass("z-label"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-lineitem").children().hasClass("z-button"));
	}

	@Test
	public void pointIconSclass() {
		connect("/layouts/iLineitem/pointIconSclass");
		assertTrue(jq(".z-lineitem-point-inner").hasClass("z-icon-home"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-lineitem-point-inner").hasClass("z-icon-user"));
	}

	@Test
	public void pointImageContent() {
		connect("/layouts/iLineitem/pointImageContent");
		String oldStyle = jq(".z-lineitem-point-inner").attr("style");
		click(jq("@button"));
		waitResponse();
		assertNotEquals(oldStyle, jq(".z-lineitem-point-inner").attr("style"));
	}

	@Test
	public void pointImageSrc() {
		connect("/layouts/iLineitem/pointImageSrc");
		assertTrue(jq(".z-lineitem-point-inner").attr("style").contains("ZK-Logo.gif"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-lineitem-point-inner").attr("style").contains("ZK-Logo-old.gif"));
	}

	@Test
	public void pointStyle() {
		connect("/layouts/iLineitem/pointStyle");
		assertTrue(jq(".z-lineitem-point").attr("style").contains("red"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-lineitem-point").attr("style").contains("blue"));
	}

	@Test
	public void pointVisible() {
		connect("/layouts/iLineitem/pointVisible");
		assertTrue(jq(".z-lineitem-point").hasClass("z-lineitem-point-hidden"));
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-lineitem-point").hasClass("z-lineitem-point-hidden"));
	}
}