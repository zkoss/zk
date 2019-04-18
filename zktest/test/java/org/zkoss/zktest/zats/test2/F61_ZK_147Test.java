/* F61_ZK_147Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 16 17:43:12 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F61_ZK_147Test extends WebDriverTestCase {
	@Test
	public void testNoModel() {
		connect();

		verifyGroupUngroup(jq("@listheader:eq(3)"), true, true, true, false);
		verifyGroupUngroup(jq("@listheader:eq(2)"), true, true, true, false);
		verifyGroupUngroup(jq("@listheader:eq(1)"), true, true, true, false);
		verifyGroupUngroup(jq("@listheader:eq(0)"), true, true, true, false);

		click(jq("@menuitem:contains(Group)"));
		waitResponse();
		Assert.assertEquals(2, jq("@listbox:first @listgroup").length());

		verifyGroupUngroup(jq("@listheader:eq(3)"), false, true, true, true);
		verifyGroupUngroup(jq("@listheader:eq(2)"), false, true, true, true);
		verifyGroupUngroup(jq("@listheader:eq(1)"), false, true, true, true);
		verifyGroupUngroup(jq("@listheader:eq(0)"), false, true, true, true);

		click(jq("@menuitem:contains(Ungroup)"));
		waitResponse();
		Assert.assertEquals(0, jq("@listbox:first @listgroup").length());

		verifyGroupUngroup(jq("@listheader:eq(3)"), true, true, true, false);
		verifyGroupUngroup(jq("@listheader:eq(2)"), true, true, true, false);
		verifyGroupUngroup(jq("@listheader:eq(1)"), true, true, true, false);
		verifyGroupUngroup(jq("@listheader:eq(0)"), true, true, true, false);
	}

	@Test
	public void testGroupModel() {
		connect();

		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(4)"), true, true, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(3)"), true, true, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(2)"), true, true, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(1)"), true, true, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(0)"), true, true, true, true);

		click(jq("@menuitem:contains(Ungroup)"));
		waitResponse();
		Assert.assertEquals(0, jq("@listbox:eq(1) @listgroup").length());

		// Step 9: every listheader has Group. In fact only first listheader has it.
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(4)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(3)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(2)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(1)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:eq(1) @listheader:eq(0)"), true, true, true, false);

		click(jq("@menuitem:contains(Group)"));
		waitResponse();
		Assert.assertEquals(2, jq("@listbox:eq(1) @listgroup").length());
	}

	@Test
	public void testListModel() {
		connect();

		verifyGroupUngroup(jq("@listbox:last @listheader:eq(4)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:last @listheader:eq(3)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:last @listheader:eq(2)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:last @listheader:eq(1)"), true, false, true, false);
		verifyGroupUngroup(jq("@listbox:last @listheader:eq(0)"), true, false, true, false);
	}

	private void verifyGroupUngroup(JQuery column, boolean verifyGroup, boolean groupVisible,
	                                boolean verifyUngroup, boolean ungroupVisible) {
		openColumnMenu(column);
		if (verifyGroup) Assert.assertEquals(groupVisible, jq("@menuitem:contains(Group)").isVisible());
		if (verifyUngroup) Assert.assertEquals(ungroupVisible, jq("@menuitem:contains(Ungroup)").isVisible());
	}

	private void openColumnMenu(JQuery column) {
		getActions().moveToElement(toElement(column))
				.moveByOffset(column.width() / 2 - 10, 0)
				.click()
				.perform();
		waitResponse();
	}
}
