/* Issue0066Test.java

	Purpose:
		
	Description:
		
	History:
		5:51 PM 2021/11/4, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Issue0066Test extends WebDriverTestCase {
	@Test
	public void testNoException() {
		connect("/stateless/issues/issue_0066.sul");

		click(jq("$btn"));
		waitResponse();
		assertEquals("OK", jq("$msg").text());
	}
}
