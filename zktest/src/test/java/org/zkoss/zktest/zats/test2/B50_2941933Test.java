/* B50_2941933Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 15:37:20 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2941933Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		rightClick(jq("@treerow:eq(0)"));
		waitResponse();
		Assertions.assertTrue(jq("@menupopup:visible").exists());
		Assertions.assertTrue(jq("@treerow:eq(0)").hasClass("z-treerow-selected"));

		rightClick(jq("@treerow:contains(alert)"));
		waitResponse();
		Assertions.assertTrue(jq(".z-messagebox-window").exists());
	}
}
