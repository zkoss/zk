/* B50_3170417_1Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 16:41:10 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3170417_1Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-treerow:eq(4) .z-tree-icon"));
		waitResponse();
		click(jq(".z-treerow:eq(5) .z-tree-icon"));
		waitResponse();
		click(jq(".z-treerow:eq(6)"));
		waitResponse();
		Assertions.assertTrue(jq(".z-paging-info:contains([ 1 - 10 / 15 ])").exists());

		click(jq(".z-button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-treerow:eq(6)").hasClass("z-treerow-selected"));
		Assertions.assertTrue(jq(".z-paging-info:contains([ 1 - 10 / 15 ])").exists());
	}
}
