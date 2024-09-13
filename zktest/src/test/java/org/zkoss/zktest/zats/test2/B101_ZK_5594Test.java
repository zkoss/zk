/* B101_ZK_5594Test.java

	Purpose:

	Description:

	History:
		4:11 PM 2024/9/13, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B101_ZK_5594Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		openColumnMenu(jq("@listheader:eq(0)"));
		click(jq("@menuitem:contains(Group)"));
		waitResponse();
		Assertions.assertTrue(3 < jq("@listgroup").length());

		openColumnMenu(jq("@listheader:eq(0)"));
		click(jq(".z-menuitem-icon.z-icon-check"));
		waitResponse();
		openColumnMenu(jq("@listheader:eq(1)"));
		waitResponse();
		click(jq(".z-menuitem.z-menuitem-checkable"));
		waitResponse();
		jq(".z-listbox-body").scrollTop(8700);
		waitResponse();

		// try three times to reproduce the issue
		for (int i = 0; i < 3; i++) {
			click(jq(".z-icon-angle-down.z-listgroup-icon-open:eq(2)"));
			waitResponse();
			assertNoZKError();
		}
	}

	private void openColumnMenu(JQuery column) {
		getActions().moveToElement(toElement(column))
				.moveByOffset(column.width() / 2 - 10, 0)
				.click()
				.perform();
		waitResponse();
	}
}
