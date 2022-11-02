/* B96_ZK_4889Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 6 18:44:32 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_4889Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery btns = jq("@button");
		String[] zkLog = new String[] {"", "", "", "", "", "", "", "", ""};
		int index = 0;
		for (JQuery btn : btns) {
			click(btn);
			waitResponse();
			zkLog[index] = getZKLog();
			closeZKLog();
			waitResponse();
			index++;
		}
		Assertions.assertEquals(9, zkLog.length);
		Assertions.assertEquals("test1 param - number: -1", zkLog[0]);
		Assertions.assertEquals("test1 param - number: 2", zkLog[1]);
		Assertions.assertEquals("test1 param - number: -1", zkLog[2]);
		Assertions.assertEquals("test1 param - number: -1", zkLog[3]);
		Assertions.assertEquals("test1 param - number: 2", zkLog[4]);
		Assertions.assertEquals("test2 param - number: -1", zkLog[5]);
		Assertions.assertEquals("test2 param - number: 2", zkLog[6]);
		Assertions.assertEquals("test3 param - count: 1, param - number: -1", zkLog[7]);
		Assertions.assertEquals("test3 param - count: 1, param - number: 1", zkLog[8]);
	}
}
