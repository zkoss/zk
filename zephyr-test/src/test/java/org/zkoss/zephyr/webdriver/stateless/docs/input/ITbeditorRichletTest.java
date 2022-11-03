/* ITbeditorRichletTest.java

	Purpose:

	Description:

	History:
		Fri Mar 04 18:45:24 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.ITbeditor} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Tbeditor">Tbeditor</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ITbeditor
 */
public class ITbeditorRichletTest extends WebDriverTestCase {
	@Test
	public void customizedProperties() {
		connect("/input/itbeditor/customizedProperties");
		assertTrue(jq(".z-tbeditor-button-pane .z-tbeditor-button-group ").length() == 3);
	}

	@Test
	public void value() {
		connect("/input/itbeditor/value");
		assertEquals("testing", jq(".z-tbeditor-editor").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("test", jq(".z-tbeditor-editor").text());
	}
}