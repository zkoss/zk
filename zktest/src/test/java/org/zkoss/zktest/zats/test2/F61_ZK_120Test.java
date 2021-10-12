/* F61_ZK_120Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 16 16:15:38 CST 2019, Created by rudyhuang

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
public class F61_ZK_120Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		openColumnMenu(jq("@listheader:eq(0)"));
		click(jq("@menuitem:contains(Sort Ascending)"));
		waitResponse();
		Assert.assertEquals("Michael Greenberg", jq("@listcell:first").text());

		openColumnMenu(jq("@listheader:eq(0)"));
		click(jq("@menuitem:contains(Sort Descending)"));
		waitResponse();
		Assert.assertEquals("Rick Perlstein", jq("@listcell:first").text());

		openColumnMenu(jq("@listheader:eq(0)"));
		click(jq("@menuitem:contains(Group)"));
		waitResponse();
		Assert.assertEquals(3, jq("@listgroup").length());

		openColumnMenu(jq("@listheader:eq(0)"));
		click(jq("@menuitem:contains(Author)"));
		waitResponse();
		Assert.assertEquals(0, jq("@listheader:contains(Author)").width());
	}

	private void openColumnMenu(JQuery column) {
		getActions().moveToElement(toElement(column))
				.moveByOffset(column.width() / 2 - 10, 0)
				.click()
				.perform();
		waitResponse();
	}
}
