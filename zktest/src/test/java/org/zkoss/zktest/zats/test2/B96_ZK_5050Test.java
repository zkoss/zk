/* B96_ZK_5050Test.java

	Purpose:
		
	Description:
		
	History:
		12:28 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5050Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(5, jq(".z-treerow").length());

		click(jq("@button"));
		waitResponse();
		assertEquals(5, jq(".z-treerow").length());
	}
}
