/* ActionVariableRichletTest.java

	Purpose:
		
	Description:
		
	History:
		3:12 PM 2022/1/3, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.auxiliary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit tests for {@link org.zkoss.zephyr.annotation.ActionVariable}
 * @author jumperchen
 * @see org.zkoss.zephyr.annotation.ActionVariable
 */
public class ActionVariableRichletTest extends WebDriverTestCase {
	@Test
	public void testBasic() {
		connect("/auxiliary/actionVariable");
		click(jq("button:contains(Submit)"));
		waitResponse();
		assertEquals("foo@foo.com foo@foo.com foo@foo.com foo@foo.com", jq("$msg").text());
	}
	@Test
	public void testCombination() {
		connect("/auxiliary/actionVariable/combination");
		click(jq("button:contains(Submit)"));
		waitResponse();
		assertEquals("foo@foo.com foo@foo.com foo@foo.com", jq("$msg").text());
	}
}
