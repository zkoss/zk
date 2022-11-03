/* ILabelElementRichletTest.java

	Purpose:

	Description:

	History:
		Thu Mar 10 16:43:53 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ILabelElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ILabelElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/LabelElement">ILabelElement</a>,
 * if any.
 *
 * @author katherine
 * @see ILabelElement
 */
public class ILabelElementRichletTest extends WebDriverTestCase {
	@Test
	public void label() {
		connect("/base_components/iLabelElement/label");
		assertEquals("label", jq(".z-caption").text());
		assertEquals("label", jq(".z-button:eq(0)").text());
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("new label", jq(".z-caption").text());
		assertEquals("new label", jq(".z-button:eq(0)").text());
	}
}