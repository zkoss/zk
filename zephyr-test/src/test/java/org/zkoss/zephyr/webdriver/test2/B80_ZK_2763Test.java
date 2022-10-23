/* B80_ZK_2763Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 28 10:11:57 CST 2015, Created by christopher

Copyright (C)  2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author christopher
 */
public class B80_ZK_2763Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		try {
			connect();
			sleep(2000);
			JQuery labels = jq("@label");
			List<String> oldValues = new ArrayList<String>();
			for (int i = 1; i < labels.length(); i++) {
				oldValues.add(labels.eq(i).text());
			}
			JQuery buttons = jq("@button");
			for (int i = 0; i < buttons.length(); i++) {
				click(buttons.eq(i));
				waitResponse();
			}
			//skip the first label again
			Assertions.assertFalse(oldValues.get(0).equals(labels.eq(1).text()));
			Assertions.assertFalse(oldValues.get(1).equals(labels.eq(2).text()));
			Assertions.assertFalse(oldValues.get(2).equals(labels.eq(3).text()));
			Assertions.assertTrue(oldValues.get(3).equals(labels.eq(4).text()));
		} catch (Exception e) {
			fail();
		}
		assertNoAnyError();
	}
}

