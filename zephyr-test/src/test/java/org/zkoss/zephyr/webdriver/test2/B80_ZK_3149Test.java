/* B80_ZK_3136Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 31 16:01:32 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3149Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery textboxes = jq("@textbox");
		JQuery buttons = jq("@button");
		assertEquals("handled", textboxes.eq(1).val());
		type(textboxes.eq(0), "aaa");
		waitResponse();
		click(buttons.get(1));
		waitResponse();
		assertEquals("Peter", textboxes.eq(0).val());
	}
}
