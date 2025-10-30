/* B90_ZK_4513Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 25 17:37:58 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4513Test extends WebDriverTestCase {
	@Test
	public void testReplaceModel() {
		connect();
		testMethod(0);
	}

	@Test
	public void testClearModel() {
		connect();
		testMethod(1);
	}

	@Test
	public void testInvalidatePartial() {
		connect();
		testMethod(2);
	}

	private void testMethod(int index) {
		click(jq("@button:contains(hide)"));
		waitResponse();
		click(jq("@button:contains(show +)").eq(index));
		waitResponse();
		Assertions.assertTrue(jq("@listbox").isVisible(),
				"Listbox should be visible");
	}
}
