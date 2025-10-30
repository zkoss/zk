/* B50_3095453Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 14:21:08 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3095453Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button"));
		waitResponse();

		JQuery root = jq(".z-treerow:eq(0)");
		assertTrue(root.find(".z-icon-caret-right").exists());

		click(jq(".z-tree-icon"));
		waitResponse();

		assertTrue(root.find(".z-icon-caret-down").exists());

		JQuery treerow = root.next();
		for (int i = 0; i < 10; i++) {
			assertTrue(treerow.is(":contains(item " + i + ")"));
			treerow = treerow.next();
		}
	}
}
