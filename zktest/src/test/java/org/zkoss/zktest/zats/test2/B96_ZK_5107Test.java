/* B96_ZK_5107Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 28 14:45:46 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5107Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String pencilClz = ".z-icon-pencil";
		click(jq(pencilClz));
		waitResponse();
		String first = ".z-treerow-checkable.z-treerow-checkbox:eq(0)";
		click(jq(first));
		waitResponse();
		assertTrue(jq(jq(first).toWidget()).hasClass("z-treerow-selected"));

		click(jq(pencilClz));
		waitResponse();
		String second = ".z-treerow-checkable.z-treerow-checkbox:eq(1)";
		click(jq(second));
		waitResponse();
		assertTrue(jq(jq(second).toWidget()).hasClass("z-treerow-selected"));
	}
}