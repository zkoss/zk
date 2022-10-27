/* ICascaderRichletTest.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:17:20 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.zpr.ICascader} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Cascader">Cascader</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.ICascader
 */
public class ICascaderRichletTest extends WebDriverTestCase {
	@Test
	public void open() {
		connect("/input/iCascader/open");
		assertFalse(jq(".z-cascader-popup").isVisible());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-cascader-popup").isVisible());
	}

	@Test
	public void placeholder() {
		connect("/input/iCascader/placeholder");
		assertEquals("test", jq(".z-cascader-placeholder").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("", jq(".z-cascader-placeholder").text());
	}

	@Test
	public void disabled() {
		connect("/input/iCascader/disabled");
		assertTrue(jq(".z-cascader.z-cascader-disabled").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-cascader.z-cascader-disabled").exists());
	}

	@Test
	public void model() {
		connect("/input/iCascader/model");
		click(jq(".z-cascader-icon.z-icon-caret-down"));
		waitResponse();
		assertEquals(4, jq(".z-cascader-cave:eq(0) .z-cascader-item").length());
		click(jq(".z-cascader-icon.z-icon-caret-right:eq(1)"));
		waitResponse();
		assertEquals(2, jq(".z-cascader-cave:eq(1) .z-cascader-item").length());
		click(jq(".z-cascader-icon.z-icon-caret-right:eq(2)"));
		waitResponse();
		assertEquals(5, jq(".z-cascader-cave:eq(2) .z-cascader-item").length());
	}
}