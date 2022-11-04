/* F02581Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Jan 07, 2015  5:26:43 PM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zephyr.webdriver.mvvm.issue;


import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class F02581Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$win2 $l1");
		JQuery l2 = jq("$win2 $l2");
		JQuery reload = jq("$reload");

		String val1;
		String val2;

		for (int i = 0; i < 4; i++) {
			val1 = l1.text();
			val2 = l2.text();
			click(reload);
			waitResponse();
			l1 = jq("$win2 $l1");
			l2 = jq("$win2 $l2");
			assertTrue(val1.equals(l1.text()));
			assertTrue(val2.equals(l2.text()));

			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
	}


	@Test
	public void testChange() {
		connect();

		JQuery l1 = jq("$win2 $l1");
		JQuery l2 = jq("$win2 $l2");
		JQuery reload = jq("$reload2");

		String val1;
		String val2;

		for (int i = 0; i < 4; i++) {
			val1 = l1.text();
			val2 = l2.text();
			click(reload);
			waitResponse();
			l1 = jq("$win2 $l1");
			l2 = jq("$win2 $l2");
			assertNotEquals(val1, l1.text());
			assertNotEquals(val2, l2.text());

			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
	}
}