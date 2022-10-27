/* InitTest.java
	Purpose:

	Description:

	History:
		Thu May 06 16:43:47 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.initialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class InitTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		String[] blockIds = { "d1", "d2" };
		for (int i = 0; i < blockIds.length; i++) {
			JQuery labels = jq("$" + blockIds[i] + " @label");
			assertEquals("test", labels.eq(0).text());
			assertEquals("123", labels.eq(1).text());
			assertEquals("test", labels.eq(2).text());
			assertTrue(jq("$" + blockIds[i] + " @checkbox").hasClass("z-checkbox-on"));
		}
	}
}
