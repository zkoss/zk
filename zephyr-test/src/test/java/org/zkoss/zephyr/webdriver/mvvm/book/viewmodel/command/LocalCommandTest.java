/* LocalCommandTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 16:03:52 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.command;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class LocalCommandTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/viewmodel/command/local-command.zul");
		JQuery jqBtn = jq("@button");
		for (int i = 0; i < 6; i++) {
			click(jqBtn.eq(i));
			waitResponse();
		}
		String[] zkLogArr = getZKLog().split("\n");
		assertEquals(6, zkLogArr.length);
		assertEquals("command1", zkLogArr[0]);
		assertEquals("commandTwo", zkLogArr[1]);
		assertEquals("command3And4", zkLogArr[2]);
		assertEquals("command3And4", zkLogArr[3]);
		MatcherAssert.assertThat(zkLogArr[4], startsWith("command5: [MouseEvent onClick"));
		assertEquals("Command [command999] unknown!", zkLogArr[5]);
	}

	@Test
	public void testDuplicated() {
		connect("/mvvm/book/viewmodel/command/local-command-duplicated.zul");
		click(jq("@button"));
		waitResponse();
		MatcherAssert.assertThat(jq(".z-messagebox .z-label").text(),
				startsWith("there are more than one Command method command1 in class"));
	}

	@Test
	public void testDefaultDuplicated() {
		connect("/mvvm/book/viewmodel/command/local-command-duplicated-default.zul");
		click(jq("@button"));
		waitResponse();
		MatcherAssert.assertThat(jq(".z-messagebox .z-label").text(),
				startsWith("there are more than one DefaultCommand method in class"));
	}
}
