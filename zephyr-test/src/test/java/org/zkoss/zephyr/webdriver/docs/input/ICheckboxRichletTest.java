/* ICheckboxRichletTest.java

	Purpose:

	Description:

	History:
		Tue Mar 01 15:17:33 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.ICheckbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Checkbox">Checkbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ICheckbox
 */
public class ICheckboxRichletTest extends WebDriverTestCase {
	@Test
	public void defaultMold() {
		connect("/input/icheckbox/mold/default");
		assertTrue(jq(".z-checkbox-default.z-checkbox-off").exists());
		assertTrue(jq(".z-checkbox-default.z-checkbox-on").exists());
	}

	@Test
	public void switchMold() {
		connect("/input/icheckbox/mold/switch");
		assertTrue(jq(".z-checkbox-switch.z-checkbox-switch-off").exists());
		assertTrue(jq(".z-checkbox-switch.z-checkbox-switch-on").exists());
	}

	@Test
	public void toggle() {
		connect("/input/icheckbox/mold/toggle");
		assertTrue(jq(".z-checkbox-toggle.z-checkbox-toggle-off").exists());
		assertTrue(jq(".z-checkbox-toggle.z-checkbox-toggle-on").exists());
	}

	@Test
	public void tristate() {
		connect("/input/icheckbox/mold/tristate");
		assertTrue(jq(".z-checkbox-tristate.z-checkbox-tristate-off").exists());
		assertTrue(jq(".z-checkbox-tristate.z-checkbox-tristate-indeterminate").exists());
		assertTrue(jq(".z-checkbox-tristate.z-checkbox-tristate-on").exists());
	}

	@Test
	public void image() {
		connect("/input/icheckbox/image");
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-checkbox-content img").attr("src"));
		click(jq("@button"));
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo-old.gif", jq(".z-checkbox-content img").attr("src"));
	}

	@Test
	public void disabled() {
		connect("/input/icheckbox/disabled");
		assertEquals("disabled", jq(".z-checkbox input").attr("disabled"));
		click(jq("@button"));
		waitResponse();
		assertEquals("null", jq(".z-checkbox input").attr("disabled"));
	}
}