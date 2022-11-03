/* INumberInputElementRichletTest.java

	Purpose:

	Description:

	History:
		Wed Feb 23 17:50:27 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.INumberInputElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link INumberInputElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/NumberInputElement">NumberInputElement</a>,
 * if any.
 *
 * @author katherine
 * @see INumberInputElement
 */
public class INumberInputElementRichletTest extends WebDriverTestCase {
	@Test
	public void locale() {
		connect("/base_components/iNumberInputElement/locale");
		assertEquals("2,000.02", jq(".z-decimalbox:eq(0)").val());
		assertEquals("2\u00A0000,02", jq(".z-decimalbox:eq(1)").val());
		assertEquals("2.000,02", jq(".z-decimalbox:eq(2)").val());
		assertEquals("2,000.02", jq(".z-doublebox:eq(0)").val());
		assertEquals("2\u00A0000,02", jq(".z-doublebox:eq(1)").val());
		assertEquals("2.000,02", jq(".z-doublebox:eq(2)").val());
		assertEquals("2,000.02", jq(".z-doublespinner-input:eq(0)").val());
		assertEquals("2\u00A0000,02", jq(".z-doublespinner-input:eq(1)").val());
		assertEquals("2.000,02", jq(".z-doublespinner-input:eq(2)").val());
	}

	@Test
	public void roundingMode() {
		connect("/base_components/iNumberInputElement/roundingMode");
		assertEquals("1.3", jq(".z-decimalbox:eq(0)").val());
		assertEquals("1.2", jq(".z-decimalbox:eq(1)").val());
		assertEquals("1.3", jq(".z-doublebox:eq(0)").val());
		assertEquals("1.2", jq(".z-doublebox:eq(1)").val());
		assertEquals("1.3", jq(".z-doublespinner-input:eq(0)").val());
		assertEquals("1.2", jq(".z-doublespinner-input:eq(1)").val());
	}
}