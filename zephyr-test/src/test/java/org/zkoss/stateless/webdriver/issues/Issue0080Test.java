/* Issue0080Test.java

	Purpose:
		
	Description:
		
	History:
		4:51 PM 2021/11/3, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Issue0080Test extends WebDriverTestCase {
	@Test
	public void testNoException() {
		connect("/issues/issue_0080.sul");

		click(jq("$btn"));
		waitResponse();
		assertEquals("OK", jq("$text").val());
	}
}
