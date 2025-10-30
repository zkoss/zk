/* B50_3025339Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 15:46:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_3025339Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		handleTimebox(jq("@timebox:eq(0)"));
		handleTimebox(jq("@timebox:eq(1)"));
		handleSpinner(jq("@spinner:eq(0)"));
		handleSpinner(jq("@spinner:eq(1)"));
	}

	private void handleTimebox(JQuery comp) {
		click(widget(comp).$n("btn-up"));
		waitResponse();
		Assertions.assertEquals("1:00:00 AM", comp.find("input").val());
		click(widget(comp).$n("btn-down"));
		waitResponse();
		Assertions.assertEquals("12:00:00 AM", comp.find("input").val());
	}

	private void handleSpinner(JQuery comp) {
		click(widget(comp).$n("btn-up"));
		waitResponse();
		Assertions.assertEquals("1", comp.find("input").val());
		click(widget(comp).$n("btn-down"));
		waitResponse();
		Assertions.assertEquals("0", comp.find("input").val());
	}
}
