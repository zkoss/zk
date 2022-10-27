/* IRadiogroupRichletTest.java

	Purpose:

	Description:

	History:
		Mon Mar 07 18:05:44 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IRadiogroup;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IRadiogroup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Radiogrouop">Radiogrouop</a>,
 * if any.
 *
 * @author katherine
 * @see IRadiogroup
 */
public class IRadiogroupRichletTest extends WebDriverTestCase {
	@Test
	public void example() {
		connect("/input/iradiogroup/example");
		click(jq(".z-radio"));
		waitResponse();
		assertEquals(" You have selected :Apple", jq(".z-label").text());
	}

	@Test
	public void ancestor() {
		connect("/input/iradiogroup/example/ancestor");
		click(jq(".z-radio"));
		click(jq(".z-radio:eq(3)"));
		waitResponse();
		assertEquals("null", jq(".z-radio input").attr("checked"));
	}

	@Test
	public void name() {
		connect("/input/iradiogroup/name");
		assertEquals("rg1", jq(".z-radio:eq(0) input").attr("name"));
		assertEquals("rg1", jq(".z-radio:eq(1) input").attr("name"));
		click(jq("@button"));
		waitResponse();
		assertEquals("rg2", jq(".z-radio:eq(0) input").attr("name"));
		assertEquals("rg2", jq(".z-radio:eq(1) input").attr("name"));
	}

	@Test
	public void orient() {
		connect("/input/iradiogroup/orient");
		assertTrue(jq(".z-radiogroup-vertical").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-radiogroup-horizontal").exists());
	}
}