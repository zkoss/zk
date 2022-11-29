/* B96_ZK_4778Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 17:25:57 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4778Test extends WebDriverTestCase {
	@Test
	public void testLeak1() {
		connect();
		sleep(2000);
		click(jq("$btnCreate"));
		waitResponse();
		click(jq("$btnDestroyLeak"));
		waitResponse();
		click(jq("$btnCheck"));
		waitResponse();
		final String[] zkLogs = getZKLog().split("\n");
		Assertions.assertEquals("eq.isIdle(): true", zkLogs[1]);
	}

	@Test
	public void testLeak2() {
		connect();
		sleep(2000);
		click(jq("$btnCreate"));
		waitResponse();
		click(jq("$btnDestroy2"));
		waitResponse();
		click(jq("$btnCheck"));
		waitResponse();
		final String[] zkLogs = getZKLog().split("\n");
		Assertions.assertEquals("eq.isIdle(): true", zkLogs[1]);
	}

	@Test
	public void testLeak3() {
		connect();
		sleep(2000);
		click(jq("$btnCreate"));
		waitResponse();
		click(jq("$btnDestroyNoLeak"));
		waitResponse();
		click(jq("$btnCheck"));
		waitResponse();
		final String[] zkLogs = getZKLog().split("\n");
		Assertions.assertEquals("eq.isIdle(): true", zkLogs[1]);
	}
}
