/* IIntboxRichletTest.java

	Purpose:

	Description:

	History:
		Mon Mar 07 14:37:26 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IIntbox;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IIntbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/IIntbox">IIntbox</a>,
 * if any.
 *
 * @author katherine
 * @see IIntbox
 */
public class IIntboxRichletTest extends WebDriverTestCase {
	@Test
	public void value() {
		connect("/input/iIntbox/value");
		assertEquals("1", jq(".z-intbox").val());
		click(jq("@button"));
		waitResponse();
		assertEquals("2", jq(".z-intbox").val());
	}
}