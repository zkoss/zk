/* B50_3013539Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 07 15:04:38 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3013539Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("$tc"));
		waitResponse();
		click(jq("@button[label=\"disabled\"]"));
		waitResponse();
		click(jq("@button[label=\"change label\"]"));
		waitResponse();
		Assertions.assertEquals("ABC", jq("$tc").text());

		click(jq("@button[label=\"disabled\"]"));
		waitResponse();
		Assertions.assertFalse(jq("@treeitem").hasClass("z-treerow-disabled"));
	}
}
