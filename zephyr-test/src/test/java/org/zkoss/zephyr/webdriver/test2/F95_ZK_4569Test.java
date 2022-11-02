/* F95_ZK_4569Test.java

	Purpose:
		
	Description:
		
	History:
		Mon May 12 16:20:11 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F95_ZK_4569Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery btns = jq("@button");
		for (JQuery btn : btns) {
			click(btn);
			waitResponse();
		}
		List<String> zkLog = Arrays.asList(getZKLog().split("\n"));
		Assertions.assertEquals(4, zkLog.size());
		Assertions.assertEquals("Clicked", zkLog.get(0));
		Assertions.assertEquals("Clicked", zkLog.get(1));
		Assertions.assertEquals("Clickedb", zkLog.get(2));
		Assertions.assertEquals("ClickedG", zkLog.get(3));
	}
}
