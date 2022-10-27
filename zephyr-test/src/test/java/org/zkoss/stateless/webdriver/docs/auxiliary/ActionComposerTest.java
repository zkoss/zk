/* ActionComposerTest.java

	Purpose:
		
	Description:
		
	History:
		9:51 AM 2022/1/6, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.auxiliary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit tests for {@link Action}
 * @author jumperchen
 */
public class ActionComposerTest extends WebDriverTestCase {
	@Test
	public void testSelector() {
		connect("/docs/auxiliary/action.sul");
		// ID Selector
		click(jq("$btn"));
		waitResponse();
		assertEquals("Clicked", jq("$msg").text());

		// Widget Name Selector
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("button 1", jq("$msg2").text());
		click(jq("@button:eq(2)"));
		waitResponse();
		assertEquals("button 2", jq("$msg2").text());
		click(jq("@button:eq(3)"));
		waitResponse();
		assertEquals("button 3", jq("$msg2").text());

		// right-click
		rightClick(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("button 1", jq("$msg2").text());
		rightClick(jq("@button:eq(2)"));
		waitResponse();
		assertEquals("button 2", jq("$msg2").text());
		rightClick(jq("@button:eq(3)"));
		waitResponse();
		assertEquals("button 3", jq("$msg2").text());
	}
}
