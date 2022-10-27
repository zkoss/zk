/* B80_ZK_2923Test.java

	Purpose:
		
	Description:
		
	History:
		12:54 PM 10/19/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B80_ZK_2923Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery textboxes = jq("@textbox");
		type(textboxes.eq(0), "abc");
		waitResponse();
		type(textboxes.eq(1), "abc2");
		waitResponse();
		click(jq("@button"));
		waitResponse();
		assertEquals("abc", textboxes.eq(0).val());
		assertEquals("", textboxes.eq(1).val());
	}
}
