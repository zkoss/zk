/* F50_3052761Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 14:30:50 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3052761Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		String thisMonth = jq("@calendar .z-calendar-title").text();
		eval(jq("@calendar") + ".trigger('mousewheel', [1])");
		waitResponse();
		eval(jq("@calendar") + ".trigger('mousewheel', [-1])");
		waitResponse();
		Assertions.assertEquals(thisMonth, jq("@calendar .z-calendar-title").text());

		click(widget("@datebox").$n("btn"));
		waitResponse();

		thisMonth = jq("@calendar:last .z-calendar-title").text();
		eval(jq("@calendar:last") + ".trigger('mousewheel', [-1])");
		waitResponse();
		eval(jq("@calendar:last") + ".trigger('mousewheel', [1])");
		waitResponse();
		Assertions.assertEquals(thisMonth, jq("@calendar:last .z-calendar-title").text());
	}
}
