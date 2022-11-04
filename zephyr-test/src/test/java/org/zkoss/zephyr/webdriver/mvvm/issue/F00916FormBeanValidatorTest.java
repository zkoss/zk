/* F00916FormBeanValidatorTest.java
	Purpose:

	Description:

	History:
		Fri Apr 30 18:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F00916FormBeanValidatorTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery saveBtn = jq("$save");
		click(saveBtn);
		waitResponse();
		JQuery msg3 = jq("$msg3");
		assertEquals("email length must large than 8", msg3.text());

		JQuery tb1 = jq("$tb1");
		type(tb1, "");
		waitResponse();
		JQuery tb2 = jq("$tb2");
		type(tb2, "");
		waitResponse();
		JQuery tb3 = jq("$tb3");
		type(tb3, "1");
		waitResponse();
		click(saveBtn);
		waitResponse();

		JQuery msg1 = jq("$msg1");
		assertEquals("name can not be null", msg1.text());
		JQuery msg2 = jq("$msg2");
		assertEquals("Last name can not be null", msg2.text());
		assertEquals("not a well-formed email address", msg3.text());


		type(tb1, "a");
		waitResponse();
		type(tb2, "b");
		waitResponse();
		type(tb3, "111@11111");
		waitResponse();
		click(saveBtn);
		waitResponse();
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());
		assertEquals("", msg3.text());
	}
}