/* F95_ZK_4582Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 22 12:05:58 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class F95_ZK_4582Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		click(jq("$btn1"));
		waitResponse();
		Assertions.assertEquals("1", jq("$lb1").text());
		click(jq("$btn2"));
		waitResponse();
		Assertions.assertEquals("one", jq("$lb1").text());
		Assertions.assertEquals("two", jq("$lb2").text());
		Assertions.assertEquals("three", jq("$lb3").text());
	}
}
