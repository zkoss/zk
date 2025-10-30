/* B50_2874709Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 16:32:02 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2874709Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-treecell:contains(1) .z-tree-icon"));
		waitResponse();
		Assertions.assertTrue(jq("@treecell:contains(3)").exists(), "3 was expected");
		Assertions.assertTrue(jq("@treecell:contains(4)").exists(), "4 was expected");

		click(jq(".z-treecell:contains(4) .z-tree-icon"));
		waitResponse();
		Assertions.assertTrue(jq("@treecell:contains(9)").exists(), "9 was expected");

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals(2, jq("@treecell").length());
		Assertions.assertTrue(jq("@treecell:contains(1)").exists(), "1 was expected");
		Assertions.assertTrue(jq("@treecell:contains(2)").exists(), "2 was expected");

		click(jq(".z-treecell:contains(1) .z-tree-icon"));
		waitResponse();
		Assertions.assertTrue(jq("@treecell:contains(3)").exists(), "3 was expected");
		Assertions.assertTrue(jq("@treecell:contains(4)").exists(), "4 was expected");

		click(jq(".z-treecell:contains(4) .z-tree-icon"));
		waitResponse();
		Assertions.assertTrue(jq("@treecell:contains(9)").exists(), "9 was expected");
		Assertions.assertTrue(jq("@treecell:contains(10)").exists(), "10 was expected");

		click(jq(".z-treecell:contains(9) .z-tree-icon"));
		waitResponse();
		Assertions.assertTrue(jq("@treecell:contains(19)").exists(), "19 was expected");
	}
}
