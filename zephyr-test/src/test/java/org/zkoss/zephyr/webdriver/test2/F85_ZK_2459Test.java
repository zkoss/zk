/* F85_ZK_2459Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 31 10:26:22 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F85_ZK_2459Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		click(jq("$btn"));
		waitResponse();
		Assertions.assertEquals("one", jq("$lb1").text());
		Assertions.assertEquals("two", jq("$lb2").text());
		Assertions.assertEquals("three", jq("$lb3").text());
	}
}