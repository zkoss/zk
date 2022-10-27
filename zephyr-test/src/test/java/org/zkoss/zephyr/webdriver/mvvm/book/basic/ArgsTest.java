/* ArgsTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 26 15:41:10 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class ArgsTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/args.zul");

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery t1 = jq("$t1");
		JQuery t2 = jq("$t2");

		assertEquals("A-Arg1", l1.text());
		assertEquals("B-myarg1", l2.text());
		assertEquals("A-Arg1", t1.val());
		assertEquals("B-myarg1", t2.val());

		type(t1, "X");
		waitResponse();
		assertEquals("X-Arg2-Arg1", l1.text());
		assertEquals("B-myarg1", l2.text());
		assertEquals("X-Arg2-Arg1", t1.val());
		assertEquals("B-myarg1", t2.val());

		type(t2, "Y");
		waitResponse();
		assertEquals("X-Arg2-Arg1", l1.text());
		assertEquals("Y-myarg2-myarg1", l2.text());
		assertEquals("X-Arg2-Arg1", t1.val());
		assertEquals("Y-myarg2-myarg1", t2.val());

		click(jq("@button:eq(0)"));
		waitResponse();

		assertEquals("X-Arg2Dennis-Arg1", l1.text());
		assertEquals("Y-myarg2Chen-myarg1", l2.text());
		assertEquals("X-Arg2Dennis-Arg1", t1.val());
		assertEquals("Y-myarg2Chen-myarg1", t2.val());

		JQuery t3 = jq("$t3");
		JQuery l3 = jq("$l3");
		type(t3, "ABC");
		waitResponse();
		assertEquals("value have to equals V1", l3.text());

		type(t3, "V1");
		waitResponse();
		assertEquals("", l3.text());
		assertEquals("V1-Arg1", l1.text());

		JQuery t4 = jq("$t4");
		JQuery l4 = jq("$l4");
		type(t4, "ABC");
		waitResponse();
		assertEquals("", l4.text());
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("value have to equals V2", l4.text());
		assertEquals("V1", t3.val());

		type(t4, "V2");
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("execute cmd2", l4.text());
		assertEquals("V2-Arg1", l1.text());
		assertEquals("V2", t3.val());
	}
}
