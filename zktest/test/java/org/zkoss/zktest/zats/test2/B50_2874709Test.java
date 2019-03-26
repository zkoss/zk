/* B50_2874709Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 16:32:02 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2874709Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-treecell:contains(1) .z-tree-icon"));
		waitResponse();
		Assert.assertTrue("3 was expected", jq("@treecell:contains(3)").exists());
		Assert.assertTrue("4 was expected", jq("@treecell:contains(4)").exists());

		click(jq(".z-treecell:contains(4) .z-tree-icon"));
		waitResponse();
		Assert.assertTrue("9 was expected", jq("@treecell:contains(9)").exists());

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(2, jq("@treecell").length());
		Assert.assertTrue("1 was expected", jq("@treecell:contains(1)").exists());
		Assert.assertTrue("2 was expected", jq("@treecell:contains(2)").exists());

		click(jq(".z-treecell:contains(1) .z-tree-icon"));
		waitResponse();
		Assert.assertTrue("3 was expected", jq("@treecell:contains(3)").exists());
		Assert.assertTrue("4 was expected", jq("@treecell:contains(4)").exists());

		click(jq(".z-treecell:contains(4) .z-tree-icon"));
		waitResponse();
		Assert.assertTrue("9 was expected", jq("@treecell:contains(9)").exists());
		Assert.assertTrue("10 was expected", jq("@treecell:contains(10)").exists());

		click(jq(".z-treecell:contains(9) .z-tree-icon"));
		waitResponse();
		Assert.assertTrue("19 was expected", jq("@treecell:contains(19)").exists());
	}
}
