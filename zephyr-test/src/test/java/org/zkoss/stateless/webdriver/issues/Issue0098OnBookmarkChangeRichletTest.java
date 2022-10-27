/* Issue0098OnBookmarkChangeRichletTest.java

	Purpose:
		
	Description:
		
	History:
		4:37 PM 2022/8/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Issue0098OnBookmarkChangeRichletTest extends WebDriverTestCase {
	@Test
	public void testBookmarkChange() {
		connect("/issue0098#123456789");

		waitResponse();
		assertEquals("123456789", jq("$msg").text());
	}
}
