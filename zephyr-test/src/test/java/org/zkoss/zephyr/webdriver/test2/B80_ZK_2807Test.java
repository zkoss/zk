/* B80_ZK_2807Test.java

	Purpose:
		
	Description:
		
	History:
		11:59 AM 7/30/15, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author Christopher
 */
public class B80_ZK_2807Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery grid = jq("$grid");
		List<String> before = new ArrayList<String>();
		List<String> expectedBefore = new ArrayList<String>(Arrays.asList("false", "true", "false"));

		for (int i = 0; i < 3; i++) {
			assertEquals(expectedBefore.get(i), grid.find("@row").eq(i).find(".z-row-content .z-label").text());
		}

		click(jq("$btn1"));
		waitResponse();

		List<String> after = new ArrayList<String>();
		List<String> expectedAfter = new ArrayList<String>(Arrays.asList("true", "true", "true"));

		for (int i = 0; i < 3; i++) {
			assertEquals(expectedAfter.get(i), grid.find("@row").eq(i).find(".z-row-content .z-label").text());
		}
	}
}
