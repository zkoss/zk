/* F80_ZK_2650Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 28 14:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F80_ZK_2650Test extends WebDriverTestCase {
	@Test
	public void test() throws IOException {
		connect();
		sleep(2000);
		click(jq("@button"));
		waitResponse();
		assertEquals("myData", getZKLog());
	}
}
