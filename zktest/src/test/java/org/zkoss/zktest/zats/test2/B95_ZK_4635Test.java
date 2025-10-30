/* B95_ZK_4635Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 14 12:25:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B95_ZK_4635Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery jqTrack = jq(".z-multislider-track");
		getActions().moveToElement(toElement(jqTrack))
				.moveByOffset(jqTrack.width() / 2 - 1, 0)
				.click()
				.perform();
		waitResponse();
		assertEquals(jqTrack.width(), jq("@multislider .z-sliderbuttons-button").eq(1).positionLeft());
		jqTrack = jq(".z-rangeslider-track");
		getActions().moveToElement(toElement(jqTrack))
				.moveByOffset(jqTrack.width() / 2 - 1, 0)
				.click()
				.perform();
		waitResponse();
		assertEquals(jqTrack.width(), jq("@rangeslider .z-sliderbuttons-button").eq(1).positionLeft());
	}
}
