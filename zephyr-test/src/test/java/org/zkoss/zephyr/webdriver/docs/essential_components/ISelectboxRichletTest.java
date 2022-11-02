/* ISelectboxRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 20 14:30:55 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ISelectbox;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ISelectbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Selectbox">Selectbox</a>,
 * if any.
 *
 * @author katherine
 * @see ISelectbox
 */
public class ISelectboxRichletTest extends WebDriverTestCase {
	@Test
	public void children() {
		connect("/essential_components/iSelectbox/children");
		assertEquals(3, jq(".z-selectbox:eq(0) option").length());
		assertEquals("multiple", jq(".z-selectbox:eq(0)").attr("multiple"));
		assertEquals(3, jq(".z-selectbox:eq(1) option").length());
	}

	@Test
	public void maxlength() {
		connect("/essential_components/iSelectbox/maxlength");
		assertEquals("item...", jq(".z-selectbox option:eq(0)").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("item a", jq(".z-selectbox option:eq(0)").text());
	}

	@Test
	public void multiple() {
		connect("/essential_components/iSelectbox/multiple");
		assertEquals("multiple", jq(".z-selectbox").attr("multiple"));
		click(jq("@button"));
		waitResponse();
		assertEquals("null", jq(".z-selectbox").attr("multiple"));
	}

	@Test
	public void name() {
		connect("/essential_components/iSelectbox/name");
		assertEquals("selectbox", jq(".z-selectbox").attr("name"));
		click(jq("@button"));
		waitResponse();
		assertEquals("new selectbox", jq(".z-selectbox").attr("name"));
	}
}