/* ActionRichletTest.java

	Purpose:
		
	Description:
		
	History:
		12:03 PM 2022/1/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.auxiliary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit tests for {@link Action}
 * @author jumperchen
 * @see Action
 */
public class ActionRichletTest extends WebDriverTestCase {
	@Test
	public void testIdSelector() {
		connect("/auxiliary/action/selector/id");
		waitResponse();
		click(jq("$btn"));
		waitResponse();
		assertEquals("Clicked", jq("$msg").text());
	}

	@Test
	public void testTypeSelector() {
		connect("/auxiliary/action/selector/type");
		click(jq("@button:eq(0)"));
		waitResponse();
		assertEquals("button 1", jq("$msg2").text());
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("button 2", jq("$msg2").text());
		click(jq("@button:eq(2)"));
		waitResponse();
		assertEquals("button 3", jq("$msg2").text());

		// right-click
		rightClick(jq("@button:eq(0)"));
		waitResponse();
		assertEquals("button 1", jq("$msg2").text());
		rightClick(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("button 2", jq("$msg2").text());
		rightClick(jq("@button:eq(2)"));
		waitResponse();
		assertEquals("button 3", jq("$msg2").text());
	}
}
