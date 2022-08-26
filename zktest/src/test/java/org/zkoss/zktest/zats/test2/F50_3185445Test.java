/* F50_3185445Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 18:30:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3185445Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@treerow:eq(0)"));
		click(widget("@treerow:eq(0)").$n("open"));
		waitResponse();
		String selectedText = jq(".z-treerow-selected").text();

		click(jq("@treecol:eq(0)"));
		waitResponse();
		Assertions.assertEquals(selectedText, jq(".z-treerow-selected").text());

		click(jq("@treecol:eq(1)"));
		waitResponse();
		Assertions.assertEquals(selectedText, jq(".z-treerow-selected").text());

		click(jq("@treecol:eq(2)"));
		waitResponse();
		Assertions.assertEquals(selectedText, jq(".z-treerow-selected").text());
	}
}
