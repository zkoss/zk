/* B96_ZK_4888Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 13 09:37:07 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_4888Test extends WebDriverTestCase {
	private static final int DRAGGING_THRESHOLD = 5;

	@Test
	public void test() {
		connect();

		JQuery column1 = jq("@listheader:eq(0)");
		waitResponse();

		resizeColumn(column1, -100);
		waitResponse();
		click(jq("@button:contains(Refresh)"));
		waitResponse();
		for(int i = 0;i < 6; i++) {
			Assertions.assertEquals(jq("@listheader").eq(i).width(), jq("@listitem:eq(0) @listcell").eq(i).width(), 1);
		}
	}

	private void resizeColumn(JQuery column, int moveXoffset) {
		int columnWidth = column.outerWidth();
		getActions().moveToElement(toElement(column))
			.moveByOffset(columnWidth / 2 - DRAGGING_THRESHOLD, 0)
			.clickAndHold()
			.moveByOffset(moveXoffset, 0)
			.release()
			.perform();
	}
}
