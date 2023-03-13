/* Issue5411Test.java

	Purpose:
		
	Description:
		
	History:
		3:07 PM 2023/3/13, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.issues;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Issue5411Test extends WebDriverTestCase {
	@Test
	public void testJDKIssue() {
		connect("/stateless/issue5411");
		assertNoAnyError();
	}
}
