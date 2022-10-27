/* GlobalCommandParamsTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 11:59:03 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.advance.parameters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class GlobalCommandParamsTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		JQuery jqBtns = jq("@button");
		click(jqBtns.eq(0));
		waitResponse();
		click(jqBtns.eq(1));
		waitResponse();
		click(jqBtns.eq(2));
		waitResponse();
		assertArrayEquals(new String[] { "GlobalCommand global1 executed: global1",
				"Local Command local2 executed: local2", "GlobalCommand global2 executed: global2",
				"Local Command local2 executed: omit", "GlobalCommand global2 executed: omit" },
				getZKLog().split("\n"));
	}
}
