/* B96_ZK_4879Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 20 12:30:26 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class B96_ZK_4879Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery navs = jq("@nav");
		String menuitemSelector = "> [role=\"menuitem\"]";

		click(navs.eq(0));
		waitResponse(true);
		Assertions.assertTrue(navs.eq(0).find(menuitemSelector).is(":focus"),
				"the focus shall stay at the nav you clicked");

		click(navs.eq(1));
		waitResponse(true);
		Assertions.assertTrue(navs.eq(1).find(menuitemSelector).is(":focus"),
				"the focus shall move to the nav you clicked");

		click(navs.eq(2));
		waitResponse(true);
		Assertions.assertTrue(
				jq("@navitem").find(menuitemSelector).is(":focus"),
				"the focus shall move to the navitem inside");

		click(navs.eq(3));
		waitResponse(true);
		Assertions.assertTrue(navs.eq(3).find(menuitemSelector).is(":focus"),
				"the focus shall move to the nav you clicked");
	}
}
