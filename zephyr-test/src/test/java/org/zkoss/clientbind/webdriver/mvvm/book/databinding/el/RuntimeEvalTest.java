/* RuntimeEvalTest.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.el;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class RuntimeEvalTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		JQuery btn1 = jq("$btn1");
		JQuery result1Label = jq("$result1");
		click(btn1);
		waitResponse();
		assertEquals("command 2 called", result1Label.text());
		click(btn1);
		waitResponse();
		assertEquals("command 1 called", result1Label.text());
		click(btn1);
		waitResponse();
		assertEquals("command 2 called", result1Label.text());

		//[Step 2]
		JQuery gb1 = jq("$gb1");
		JQuery gb2 = jq("$gb2");
		JQuery gb3 = jq("$gb3");
		JQuery gb4 = jq("$gb4");
		JQuery gb5 = jq("$gb5");
		JQuery gb6 = jq("$gb6");
		assertFalse(gb1.isVisible());
		assertFalse(gb2.isVisible());
		assertFalse(gb3.isVisible());
		assertFalse(gb4.isVisible());
		assertFalse(gb5.isVisible());
		assertFalse(gb6.isVisible());
		//[Step 3]
		click(jq("$btn3"));
		waitResponse();
		assertFalse(gb1.isVisible());
		assertFalse(gb2.isVisible());
		assertFalse(gb3.isVisible());
		assertFalse(gb4.isVisible());
		assertFalse(gb5.isVisible());
		assertTrue(gb6.isVisible());
		//[Step 4]
		click(jq("$btn4"));
		waitResponse();
		assertTrue(gb1.isVisible());
		assertTrue(gb2.isVisible());
		assertTrue(gb3.isVisible());
		assertTrue(gb4.isVisible());
		assertTrue(gb5.isVisible());
		assertTrue(gb6.isVisible());
		//[Step 5]
		JQuery result5_1Label = jq("$result5_1");
		JQuery result5_2Label = jq("$result5_2");
		assertEquals("Dennis", result5_1Label.text());
		assertEquals(result5_1Label.text(), result5_2Label.text());
	}
}
