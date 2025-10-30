/* B96_ZK_4283Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 07 15:32:19 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4283Test extends WebDriverTestCase {
	@Test
	public void testGroupsModelArray() {
		connect();
		testSort();
	}

	@Test
	public void testSimpleGroupsModel() {
		connect(getTestURL("B96-ZK-4283.zul"));
		testSort();
	}

	private void testSort() {
		final JQuery lbName = jq("@listheader:contains(Name)");
		final JQuery grName = jq("@column:contains(Name)");
		click(lbName);
		waitResponse();
		Assertions.assertEquals(widget(lbName).$n("sort-icon").get("className"),
				widget(grName).$n("sort-icon").get("className"), "The sort direction was not applied");

		final JQuery lbCategory = jq("@listheader:contains(Category)");
		final JQuery grCategory = jq("@column:contains(Category)");
		click(grCategory);
		waitResponse();
		Assertions.assertEquals(widget(lbCategory).$n("sort-icon").get("className"),
				widget(grCategory).$n("sort-icon").get("className"),
				"The sort direction was not applied");
	}
}
