/* F95_ZK_4508Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 18 12:03:21 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F95_ZK_4508Test extends ZephyrClientMVVMTestCase {
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
		Assertions.assertEquals(8, zkLog.size());
		Assertions.assertEquals("BindingParam, Tom (Tom)!", zkLog.get(0));
		Assertions.assertEquals("HeaderParam, keep-alive (keep-alive)!", zkLog.get(1));
		Assertions.assertEquals("ScopeParam, Tim (Tim)!", zkLog.get(3));
		assertThat(zkLog.get(4), matchesRegex("CookieParam, ([\\w.]+?) \\(\\1\\)!"));
		Assertions.assertEquals("ExecutionParam, foo (foo)!", zkLog.get(5));
		Assertions.assertEquals("ExecutionArgParam, bar (bar)!", zkLog.get(6));
		Assertions.assertEquals("QueryParam, Tony (Tony)!", zkLog.get(7));
	}
}
