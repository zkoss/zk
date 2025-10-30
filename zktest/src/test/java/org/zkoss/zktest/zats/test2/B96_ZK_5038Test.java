/* B96_ZK_5038Test.java

	Purpose:
		
	Description:
		
	History:
		4:07 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5038Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String first = ".z-listitem-checkable.z-listitem-checkbox:eq(0)";
		click(jq(first));
		assertTrue(jq(first).toWidget().is("selected"));

		click(jq("@button:eq(1)"));
		waitResponse();
		String second = ".z-listitem-checkable.z-listitem-checkbox:eq(1)";
		click(jq(second));
		assertTrue(jq(second).toWidget().is("selected"));
	}
}