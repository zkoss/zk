/* B96_ZK_5043Test.java

	Purpose:
		
	Description:
		
	History:
		6:01 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B96_ZK_5043Test extends WebDriverTestCase {
	@Test
	public void testMap() throws Exception {
		connect();
		sleep(2000);
		JQuery mapAgent = jq("$map");
		JQuery replace1 = mapAgent.find("@button").eq(0);
		JQuery change1 = mapAgent.find("@button").eq(1);
		JQuery value1 = mapAgent.find("@label").last();
		String value = value1.text();
		click(change1);
		waitResponse();
		assertNotEquals(value, value = value1.text());

		click(replace1);
		waitResponse();
		assertNotEquals(value, value = value1.text());

		click(change1);
		waitResponse();
		assertNotEquals(value, value1.text());
	}

	@Test
	public void testDot() throws Exception {
		connect();
		sleep(2000);
		JQuery dotAgent = jq("$dot");
		JQuery replace1 = dotAgent.find("@button").eq(0);
		JQuery change1 = dotAgent.find("@button").eq(1);
		JQuery value1 = dotAgent.find("@label").last();
		String value = value1.text();
		click(change1);
		waitResponse();
		assertNotEquals(value, value = value1.text());

		click(replace1);
		waitResponse();
		assertNotEquals(value, value = value1.text());

		click(change1);
		waitResponse();
		assertNotEquals(value, value1.text());
	}
}