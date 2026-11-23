/* F50_3095460Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 16:53:16 CST 2019, Created by rudyhuang

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
public class F50_3095460Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		checkSpan(jq("@listbox:eq(0)"), jq("@listbox:eq(1)"));
		checkHFlexMin(jq("@listbox:eq(1)"), jq("@listbox:eq(2)"));
		checkSame(jq("@listbox:eq(0)"), jq("@listbox:eq(3)"));

		checkSpan(jq("@grid:eq(0)"), jq("@grid:eq(1)"));
		checkHFlexMin(jq("@grid:eq(1)"), jq("@grid:eq(2)"));
		checkSame(jq("@grid:eq(0)"), jq("@grid:eq(3)"));
	}

	private void checkSpan(JQuery spanned, JQuery original) {
		JQuery columnsSpan = spanned.find("th");
		JQuery columnsOrig = original.find("th");
		for (int i = 0; i < 4; i++) {
			Assert.assertTrue(columnsSpan.eq(i).width() > columnsOrig.eq(i).width());
		}
	}

	private void checkHFlexMin(JQuery orig, JQuery flexed) {
		JQuery columnsOrig = orig.find("th");
		JQuery columnsFlex = flexed.find("th");
		for (int i = 0; i < 4; i++) {
			Assert.assertEquals(columnsFlex.eq(i).width(), columnsOrig.eq(i).width(), 2);
		}

		Assert.assertEquals(flexed.width(), jq(widget(flexed).$n("head")).width(), 2);
	}

	private void checkSame(JQuery mesh1, JQuery mesh2) {
		JQuery columns1 = mesh1.find("th");
		JQuery columns2 = mesh2.find("th");
		for (int i = 0; i < 4; i++) {
			Assert.assertEquals(columns1.eq(i).width(), columns2.eq(i).width(), 2);
		}
	}
}
