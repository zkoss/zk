/* DefaultParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 12:47:43 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.advance.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class DefaultParamTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		JQuery jqButtons = jq("@button");
		click(jqButtons.get(0));
		waitResponse();
		click(jqButtons.get(1));
		waitResponse();
		assertEquals("test param: -1\ntest param: 2", getZKLog());
	}

	@Test
	public void omitTest() {
		connect();
		JQuery jqButtons = jq("@button");
		click(jqButtons.get(2));
		waitResponse();
		click(jqButtons.get(3)); // TODO: bug?
		waitResponse();
		assertEquals("test param: -1\ntest param: 2", getZKLog());
	}
}
