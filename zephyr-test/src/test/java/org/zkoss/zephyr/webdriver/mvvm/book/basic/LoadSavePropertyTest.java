/* LoadSavePropertyTest.java
	Purpose:

	Description:

	History:
		Tue Oct 05 11:11:58 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class LoadSavePropertyTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/load-save-property.zul");
		JQuery t21 = jq("$t21");
		JQuery t22 = jq("$t22");
		JQuery t23 = jq("$t23");

		assertEquals("A", jq("$l11").text());
		assertEquals("B", jq("$l12").text());
		assertEquals("C", jq("$l13").text());
		assertEquals("A", jq("$l14").text());
		assertEquals("B", jq("$l15").text());
		assertEquals("C", jq("$l16").text());
		type(t21, "X");
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("X", jq("$l12").text());
		assertEquals("C", jq("$l13").text());
		assertEquals("X", jq("$l14").text());
		assertEquals("X", jq("$l15").text());
		assertEquals("X", jq("$l16").text());
		type(t22, "Y");
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("X", jq("$l12").text());
		assertEquals("Y", jq("$l13").text());
		assertEquals("Y", jq("$l14").text());
		assertEquals("X", jq("$l15").text());
		assertEquals("Y", jq("$l16").text());
		type(t23, "Z");
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("X", jq("$l12").text());
		assertEquals("Y", jq("$l13").text());
		assertEquals("Z", jq("$l14").text());
		assertEquals("X", jq("$l15").text());
		assertEquals("Y", jq("$l16").text());
		click(jq("$btn1"));
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("Z", jq("$l12").text());
		assertEquals("Y", jq("$l13").text());
		assertEquals("Z", jq("$l14").text());
		assertEquals("Z", jq("$l15").text());
		assertEquals("Z", jq("$l16").text());
		type(t23, "G");
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("Z", jq("$l12").text());
		assertEquals("Y", jq("$l13").text());
		assertEquals("G", jq("$l14").text());
		assertEquals("Z", jq("$l15").text());
		assertEquals("Z", jq("$l16").text());
		click(jq("$btn2"));
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("Z", jq("$l12").text());
		assertEquals("G", jq("$l13").text());
		assertEquals("G", jq("$l14").text());
		assertEquals("Z", jq("$l15").text());
		assertEquals("G", jq("$l16").text());
		type(t23, "H");
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("Z", jq("$l12").text());
		assertEquals("G", jq("$l13").text());
		assertEquals("H", jq("$l14").text());
		assertEquals("Z", jq("$l15").text());
		assertEquals("G", jq("$l16").text());
		click(jq("$btn3"));
		waitResponse();
		assertEquals("A", jq("$l11").text());
		assertEquals("Z", jq("$l12").text());
		assertEquals("H", jq("$l13").text());
		assertEquals("H", jq("$l14").text());
		assertEquals("Z", jq("$l15").text());
		assertEquals("H", jq("$l16").text());
	}
}
