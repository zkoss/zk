/* F00862BeanValidator4FormTest.java
	Purpose:

	Description:

	History:
		Fri Apr 30 18:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F00862BeanValidator4FormTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tb1 = jq("$tb1");
		click(tb1);
		waitResponse();
		sendKeys(tb1, Keys.chord(isMac() ? Keys.COMMAND : Keys.CONTROL, "A"), Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		JQuery msg1 = jq("$msg1");
		assertEquals("name can not be null", msg1.text());
		type(tb1, "a");
		waitResponse();
		assertEquals("", msg1.text());

		JQuery tb2 = jq("$tb2");
		click(tb2);
		waitResponse();
		sendKeys(tb2, Keys.chord(isMac() ? Keys.COMMAND : Keys.CONTROL, "A"), Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		JQuery msg2 = jq("$msg2");
		assertEquals("Last name can not be null", msg2.text());
		type(tb2, "b");
		waitResponse();
		assertEquals("", msg2.text());

		JQuery tb3 = jq("$tb3");
		type(tb3, "1");
		waitResponse();
		JQuery msg3 = jq("$msg3");
		assertEquals("not a well-formed email address", msg3.text());
		type(tb3, "111@111");
		waitResponse();
		assertEquals("email length must large than 8", msg3.text());
		type(tb3, "111@1111");
		waitResponse();
		assertEquals("", msg3.text());
	}
}