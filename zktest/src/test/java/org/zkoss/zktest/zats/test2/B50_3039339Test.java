/* B50_3039339Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 17:05:13 CST 2019, Created by rudyhuang

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
public class B50_3039339Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		handleHorizontalScroll(jq("@grid:eq(0)"));
		handleHorizontalScroll(jq("@grid:eq(1)"));

		handleVerticalScroll(jq("@grid:eq(0)"));
		handleVerticalScroll(jq("@grid:eq(1)"));
		handleVerticalScroll(jq("@grid:eq(2)"));
	}

	private void handleHorizontalScroll(JQuery comp) {
		jq(widget(comp).$n("body")).scrollLeft(9999);
		waitResponse();

		int headerLeft = comp.find("@column:last").offsetLeft();
		int contentLeft = comp.find("@row:first td:last").offsetLeft();
		Assert.assertEquals(headerLeft, contentLeft, 2);
	}

	private void handleVerticalScroll(JQuery comp) {
		jq(widget(comp).$n("body")).scrollTop(9999);
		waitResponse();

		int headerLeft = comp.find("@column:last").offsetLeft();
		int contentLeft = comp.find("@row:last td:last").offsetLeft();
		Assert.assertEquals(headerLeft, contentLeft, 2);
	}
}
