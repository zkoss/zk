/* IRadioRichletTest.java

	Purpose:

	Description:

	History:
		Mon Mar 07 17:51:11 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IRadio;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IRadio} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Radio">Radio</a>,
 * if any.
 *
 * @author katherine
 * @see IRadio
 */
public class IRadioRichletTest extends WebDriverTestCase {
	@Test
	public void radiogroup() {
		connect("/input/iRadio/radiogroup");
		click(jq("@button"));
		waitResponse();
		assertEquals(jq(".z-radio:eq(1) input").attr("name"),
				jq(".z-radio:eq(0) input").attr("name"));
	}

	@Test
	public void selected() {
		connect("/input/iRadio/selected");
		assertEquals("checked", jq(".z-radio input").attr("checked"));
		click(jq("@button"));
		waitResponse();
		assertEquals("null", jq(".z-radio input").attr("checked"));
	}
}