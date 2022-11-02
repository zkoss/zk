/* IDecimalboxRichletTest.java

	Purpose:

	Description:

	History:
		Wed Mar 02 15:42:44 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import org.zkoss.stateless.sul.IDecimalbox;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IDecimalbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Decimalbox">Decimalbox</a>,
 * if any.
 *
 * @author katherine
 * @see IDecimalbox
 */
public class IDecimalboxRichletTest extends WebDriverTestCase {
	@Test
	public void scale() {
		connect("/input/iDecimalbox/scale");
		JQuery box = jq(".z-decimalbox");
		type(box, "0.123");
		waitResponse();
		assertEquals("0.12", jq(".z-decimalbox").val());
		click(jq("@button"));
		waitResponse();
		WebElement input = toElement(jq(".z-decimalbox"));
		input.clear();
		type(box, "0.123");
		assertEquals("0.1", jq(".z-decimalbox").val());
	}

	@Test
	public void value() {
		connect("/input/iDecimalbox/value");
		assertEquals("0.12", jq(".z-decimalbox").val());
		click(jq("@button"));
		waitResponse();
		assertEquals("0.22", jq(".z-decimalbox").val());
	}
}