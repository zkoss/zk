/* GlobalCommandTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 16:38:59 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.command;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class GlobalCommandTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/viewmodel/command/global-command.zul");
		click(jq("$btnClean"));
		waitResponse();

		String[] zkLog = getZKLog().split("\n");
		assertEquals(2, zkLog.length);
		MatcherAssert.assertThat(Arrays.asList(zkLog), containsInAnyOrder("GlobalCommandVM clean!", "command1"));
	}

	@Test
	public void testUnknown() {
		connect("/mvvm/book/viewmodel/command/global-command.zul");
		click(jq("$btnUnknown"));
		waitResponse();

		String[] zkLog = getZKLog().split("\n");
		assertEquals(2, zkLog.length);
		MatcherAssert.assertThat(Arrays.asList(zkLog),
				containsInAnyOrder("[GlobalCommandVM] GlobalCommand [unknown] unknown!",
						"[LocalCommandVM] GlobalCommand [unknown] unknown!"));
	}

	@Test
	public void testDefaultDuplicated() {
		connect("/mvvm/book/viewmodel/command/global-command-duplicated-default.zul");
		click(jq("@button"));
		waitResponse();
		MatcherAssert.assertThat(jq(".z-messagebox .z-label").text(),
				startsWith("there are more than one DefaultGlobalCommand method in class"));
	}
}
