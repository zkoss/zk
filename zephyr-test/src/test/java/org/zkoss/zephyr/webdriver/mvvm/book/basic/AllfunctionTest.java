/* AllfunctionTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 26 11:52:28 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

public class AllfunctionTest extends ZephyrClientMVVMTestCase {
	@Test
	public void warmTest() {
		connect("/mvvm/book/basic/allfunction.zul");

		click(jq("@button:contains(warn)"));
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		assertNoJSError();
		assertFalse(hasError());
	}

	@Test
	public void basicTest() {
		connect("/mvvm/book/basic/allfunction.zul");

		click(jq("@button:contains(basic)"));
		waitResponse();

		assertEquals("item 1", jq(".init-label").text(), "init binding should work");
		assertEquals("'", jq(".esc1").text(), "init binding should work");
		assertEquals("\"", jq(".esc2").text(), "init binding should work");

		type(jq("@textbox:eq(1)"), "B");
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		assertEquals("value must be 'A'", jq(".z-notification-content").text());

		click(jq(".z-label"));
		waitResponse();

		click(jq("@button:contains(save2)"));
		waitResponse();

		assertEquals("value must be 'A'", jq(".z-notification-content").text());

		type(jq("@textbox:eq(1)"), "A");
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		assertEquals("A-GCMD1", jq("@textbox:eq(0)").val());
		assertEquals("A-GCMD1", jq(".bind-label").text());
		assertEquals("A", jq(".load-label").text());

		click(jq("@button:contains(save2)"));
		waitResponse();

		assertEquals("A-GCMD2", jq("@textbox:eq(0)").val());
		assertEquals("A-GCMD2", jq(".bind-label").text());
		assertEquals("A", jq(".load-label").text());
	}

	@Test
	public void formTest() {
		connect("/mvvm/book/basic/allfunction.zul");

		click(jq("@button:contains(form)"));
		waitResponse();

		assertEquals("item 1", jq("@textbox").val());
		assertEquals("item 1", jq(".load-label1").text());
		assertEquals("item 1", jq(".load-label2").text());

		type(jq("@textbox"), "A");
		waitResponse();

		assertEquals("A", jq(".load-label1").text());
		assertEquals("item 1", jq(".load-label2").text());

		click(jq("@button:contains(save3)"));
		waitResponse();

		assertEquals("value must be 'B'", jq(".z-notification-content").text());

		type(jq("@textbox"), "B");
		waitResponse();

		assertEquals("B", jq(".load-label1").text());
		assertEquals("item 1", jq(".load-label2").text());

		click(jq("@button:contains(save3)"));
		waitResponse();

		assertEquals("B", jq(".load-label1").text());
		assertEquals("B", jq(".load-label2").text());
	}

	@Test
	public void referenceTest() {
		connect("/mvvm/book/basic/allfunction.zul");

		click(jq("@button:contains(reference)"));
		waitResponse();

		assertEquals("item 1", jq("@textbox:eq(0)").val());
		assertEquals("item 1", jq(".init-label1").text());
		assertEquals("item 1", jq(".bind-label1").text());

		assertEquals("item 1", jq("@textbox:eq(1)").val());
		assertEquals("item 1", jq(".init-label2").text());
		assertEquals("item 1", jq(".bind-label2").text());

		type(jq("@textbox:eq(0)"), "A");
		waitResponse();

		assertEquals("item 1", jq(".init-label1").text());
		assertEquals("A", jq(".bind-label1").text());
		assertEquals("A", jq("@textbox:eq(1)").val());
		assertEquals("item 1", jq(".init-label2").text());
		assertEquals("A", jq(".bind-label2").text());

		type(jq("@textbox:eq(1)"), "B");
		waitResponse();

		assertEquals("B", jq("@textbox:eq(0)").val());
		assertEquals("item 1", jq(".init-label1").text());
		assertEquals("B", jq(".bind-label1").text());
		assertEquals("item 1", jq(".init-label2").text());
		assertEquals("B", jq(".bind-label2").text());
	}

	@Test
	public void collectionTest() {
		connect("/mvvm/book/basic/allfunction.zul");

		click(jq("@button:contains(collection)"));
		waitResponse();

		assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());

		click(jq("@listitem:eq(2)"));
		waitResponse();

		assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());

		click(jq("@listitem:eq(1)"));
		waitResponse();

		assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());
	}
}
