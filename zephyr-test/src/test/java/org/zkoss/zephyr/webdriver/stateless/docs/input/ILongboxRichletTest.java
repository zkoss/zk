/* ILongboxRichletTest.java

	Purpose:

	Description:

	History:
		Mon Mar 07 15:38:25 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ILongbox;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ILongbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Longbox">Longbox</a>,
 * if any.
 *
 * @author katherine
 * @see ILongbox
 */
public class ILongboxRichletTest extends WebDriverTestCase {
	@Test
	public void value() {
		connect("/input/iLongbox/value");
		assertEquals("10", jq(".z-longbox").val());
		click(jq("@button"));
		waitResponse();
		assertEquals("20", jq(".z-longbox").val());
	}
}