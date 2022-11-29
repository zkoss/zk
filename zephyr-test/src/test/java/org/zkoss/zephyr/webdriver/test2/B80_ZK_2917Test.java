/* B80_ZK_2917Test.java

	Purpose:
		
	Description:
		
	History:
		12:20 PM 10/27/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B80_ZK_2917Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery cmdBtn = jq("$cmdBtn");
		JQuery addBtn = jq("$addBtn");
		JQuery rmBtn = jq("$rmBtn");
		JQuery msg = jq("$msg");

		click(cmdBtn);
		waitResponse();
		assertEquals("inner command triggered", msg.text());
		for (int i = 0; i < 3; i++) {
			click(rmBtn);
			waitResponse();
			assertEquals("", msg.text());

			click(cmdBtn);
			waitResponse();
			assertEquals("inner command triggered", msg.text());
			click(addBtn);
			waitResponse();
			assertEquals("", msg.text());

			click(cmdBtn);
			waitResponse();
			assertEquals("inner command triggered", msg.text());
			assertEquals("inner command triggered", msg.text());
		}

	}
}
