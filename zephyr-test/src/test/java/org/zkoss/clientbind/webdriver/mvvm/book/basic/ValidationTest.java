/* ValidationTest.java
	Purpose:

	Description:

	History:
		Tue Oct 05 11:11:58 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class ValidationTest extends ClientBindTestCase {
	@Test
	public void test1() {
		connect();
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery t21 = jq("$t21");
		JQuery t22 = jq("$t22");
		JQuery t31 = jq("$t31");
		JQuery t32 = jq("$t32");
		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");

		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("0", t31.val());
		assertEquals("", t32.val());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());

		type(t31, "1");
		waitResponse();
		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("1", t31.val());
		assertEquals("", t32.val());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());

		type(t32, "3");
		waitResponse();
		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("1", t31.val());
		assertEquals("3", t32.val());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());

		JQuery btn1 = jq("$btn1");
		click(btn1);
		waitResponse();
		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("1", t31.val());
		assertEquals("3", t32.val());
		assertEquals("value 1 have to large than 10", msg1.text());
		assertEquals("value 2 have to large than 20", msg2.text());

		type(t31, "15");
		waitResponse();
		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("15", t31.val());
		assertEquals("3", t32.val());
		assertEquals("value 1 have to large than 10", msg1.text());
		assertEquals("value 2 have to large than 20", msg2.text());

		click(btn1);
		waitResponse();
		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("15", t31.val());
		assertEquals("3", t32.val());
		assertEquals("", msg1.text());
		assertEquals("value 2 have to large than 20", msg2.text());

		type(t32, "35");
		waitResponse();
		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("15", t31.val());
		assertEquals("35", t32.val());
		assertEquals("", msg1.text());
		assertEquals("value 2 have to large than 20", msg2.text());

		click(btn1);
		waitResponse();
		assertEquals("15", l11.text());
		assertEquals("35", l12.text());
		assertEquals("15", t21.val());
		assertEquals("35", t22.val());
		assertEquals("15", t31.val());
		assertEquals("35", t32.val());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());
	}

	@Override
	protected boolean isHeadless() {
		return false;
	}

	@Test
	public void test2() {
		connect();
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery t21 = jq("$t21");
		JQuery t22 = jq("$t22");
		JQuery t31 = jq("$t31");
		JQuery t32 = jq("$t32");
		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");

		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("0", t21.val());
		assertEquals("", t22.val());
		assertEquals("0", t31.val());
		assertEquals("", t32.val());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());

		sendKeys(t21, Keys.BACK_SPACE, "1");
		blur(t21);
		waitResponse();
		assertEquals("0", l11.text());
		assertEquals("", l12.text());
		assertEquals("1", t21.val());
		assertEquals("", t22.val());
		assertEquals("0", t31.val());
		assertEquals("", t32.val());
		assertEquals("value 1 have to large than 10", msg1.text());
		assertEquals("", msg2.text());

		sendKeys(t21, Keys.BACK_SPACE, "12");
		blur(t21);
		waitResponse();
		assertEquals("12", l11.text());
		assertEquals("", l12.text());
		assertEquals("12", t21.val());
		assertEquals("", t22.val());
		assertEquals("12", t31.val());
		assertEquals("", t32.val());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());

		sendKeys(t22, "3");
		blur(t22);
		waitResponse();
		assertEquals("12", l11.text());
		assertEquals("", l12.text());
		assertEquals("12", t21.val());
		assertEquals("3", t22.val());
		assertEquals("12", t31.val());
		assertEquals("", t32.val());
		assertEquals("", msg1.text());
		assertEquals("value 2 have to large than 20", msg2.text());

		sendKeys(t22, "3");
		blur(t22);
		waitResponse();
		assertEquals("12", l11.text());
		assertEquals("33", l12.text());
		assertEquals("12", t21.val());
		assertEquals("33", t22.val());
		assertEquals("12", t31.val());
		assertEquals("33", t32.val());
		assertEquals("", msg1.text());
		assertEquals("", msg2.text());
	}
}
