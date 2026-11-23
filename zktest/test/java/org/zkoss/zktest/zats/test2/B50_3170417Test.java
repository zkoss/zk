/* B50_3170417Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 16:11:26 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3170417Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(getTreeiconByContent("Root.4"));

		JQuery treeicon40 = getTreeiconByContent("Root.4.0");
		click(treeicon40);
		verifyPagingInfo();

		click(treeicon40);
		click(treeicon40);
		verifyPagingInfo();

		JQuery treerow4 = jq(".z-treerow:contains(Root.4)");
		JQuery visibleButton = getButtonByContent("visible");
		JQuery openButton = getButtonByContent("open");

		click(visibleButton);
		Assert.assertFalse(treerow4.exists());

		click(openButton);
		click(visibleButton);
		Assert.assertTrue(treerow4.is(":last-child"));

		click(visibleButton);
		click(openButton);
		click(visibleButton);
		Assert.assertTrue(treerow4.exists());
		Assert.assertTrue(treerow4.next().exists());
	}

	private void verifyPagingInfo() {
		Assert.assertTrue(jq(".z-paging-info:contains([ 1 - 10 / 15 ])").exists());
	}

	private JQuery getTreeiconByContent(String content) {
		return jq(".z-treerow:contains(" + content + ")").find(".z-tree-icon");
	}

	private JQuery getButtonByContent(String content) {
		return jq(".z-button:contains(" + content + ")");
	}

	private void click(JQuery target) {
		super.click(target);
		waitResponse();
	}
}
