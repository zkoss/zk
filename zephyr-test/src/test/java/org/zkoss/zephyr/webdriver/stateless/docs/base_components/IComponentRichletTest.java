/* IComponentTest.java

	Purpose:
		
	Description:
		
	History:
		4:40 PM 2021/12/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IComponent;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IComponent} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/AbstractComponent">AbstractComponent</a>,
 * if any.
 * @see IComponent
 * @author jumperchen
 */
public class IComponentRichletTest extends WebDriverTestCase {
	@Test
	public void testIndex() {
		connect("/base_components/icomponent");

		// textbox with ID "msg"
		assertEquals(1, jq("$msg").length());

		// button with "Submit" label
		assertEquals(1, jq("button:contains(Submit)").length());

		// test @Action handler with onClick
		type(jq("$msg"), "zephyr");
		click(jq("button"));
		waitResponse();
		assertEquals("Hello zephyr", jq(".z-messagebox .z-label").text());
	}

	@Test
	public void testWithActions() {
		connect("/base_components/icomponent/withActions");

		// textbox with ID "myCheckbox"
		assertEquals(1, jq("$myCheckbox").length());

		// test action handler with onCheck
		click(jq("$myCheckbox"));
		waitResponse();
		assertEquals("do check", jq(".z-messagebox .z-label").text());
		click(jq("@button:contains(OK)"));
		waitResponse();

		// test action handler with onRightClick
		rightClick(jq("$myCheckbox"));
		waitResponse();
		assertEquals("do right click", jq(".z-messagebox .z-label").text());
	}
	@Test
	public void testWithVisible() {
		connect("/base_components/icomponent/withVisible");

		assertTrue(jq("@label:contains(ILabel)").exists());
		assertFalse(jq("@label:contains(ILabel)").isVisible());
	}
}
