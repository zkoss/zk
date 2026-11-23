/* B70_ZK_2960Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 17:51:11 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThan;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2960Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int listboxHeight = jq(".z-listbox-body").height();
		int listitemHeight = jq("@listitem").height();
		int itemVisibleCount = (int) Math.floor(listboxHeight / (1.0 * listitemHeight));

		JQuery bottomItem = jq(String.format("@listitem:eq(%d)", itemVisibleCount - 1)).prev();
		click(widget(bottomItem.find("@combobox:eq(0)")).$n("btn"));
		waitResponse(true);
		Assert.assertThat(jq(".z-combobox-popup.z-combobox-open").offsetTop(), lessThan(bottomItem.find("@combobox:eq(0)").offsetTop()));

		click(widget(bottomItem.find("@combobox:eq(1)")).$n("btn"));
		waitResponse(true);
		Assert.assertThat(jq(".z-combobox-popup.z-combobox-open").offsetTop(), lessThan(bottomItem.find("@combobox:eq(1)").offsetTop()));
	}
}
