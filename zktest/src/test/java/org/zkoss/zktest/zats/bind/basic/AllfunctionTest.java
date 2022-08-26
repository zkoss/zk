/* AllfunctionTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 26 11:52:28 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class AllfunctionTest extends WebDriverTestCase {
	@Test
	public void warmTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(warn)"));
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		assertNoJSError();
		Assertions.assertFalse(hasError());
	}
	@Test
	public void basicTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(basic)"));
		waitResponse();

		Assertions.assertEquals("item 1", jq(".init-label").text(), "init binding should work");
		Assertions.assertEquals("'", jq(".esc1").text(), "init binding should work");
		Assertions.assertEquals("\"", jq(".esc2").text(), "init binding should work");

		type(jq("@textbox:eq(1)"), "B");
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		Assertions.assertEquals("value must be 'A'", jq(".z-notification-content").text());

		click(jq(".z-label"));
		waitResponse();

		click(jq("@button:contains(save2)"));
		waitResponse();

		Assertions.assertEquals("value must be 'A'", jq(".z-notification-content").text());

		type(jq("@textbox:eq(1)"), "A");
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		Assertions.assertEquals("A-GCMD1", jq("@textbox:eq(0)").val());
		Assertions.assertEquals("A-GCMD1", jq(".bind-label").text());
		Assertions.assertEquals("A", jq(".load-label").text());

		click(jq("@button:contains(save2)"));
		waitResponse();

		Assertions.assertEquals("A-GCMD2", jq("@textbox:eq(0)").val());
		Assertions.assertEquals("A-GCMD2", jq(".bind-label").text());
		Assertions.assertEquals("A", jq(".load-label").text());
	}

	@Test
	public void formTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(form)"));
		waitResponse();

		Assertions.assertEquals("item 1", jq("@textbox").val());
		Assertions.assertEquals("item 1", jq(".load-label1").text());
		Assertions.assertEquals("item 1", jq(".load-label2").text());

		type(jq("@textbox"), "A");
		waitResponse();

		Assertions.assertEquals("A", jq(".load-label1").text());
		Assertions.assertEquals("item 1", jq(".load-label2").text());

		click(jq("@button:contains(save3)"));
		waitResponse();

		Assertions.assertEquals("value must be 'B'", jq(".z-notification-content").text());

		type(jq("@textbox"), "B");
		waitResponse();

		Assertions.assertEquals("B", jq(".load-label1").text());
		Assertions.assertEquals("item 1", jq(".load-label2").text());

		click(jq("@button:contains(save3)"));
		waitResponse();

		Assertions.assertEquals("B", jq(".load-label1").text());
		Assertions.assertEquals("B", jq(".load-label2").text());
	}

	@Test
	public void referenceTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(reference)"));
		waitResponse();

		Assertions.assertEquals("item 1", jq("@textbox:eq(0)").val());
		Assertions.assertEquals("item 1", jq(".init-label1").text());
		Assertions.assertEquals("item 1", jq(".bind-label1").text());

		Assertions.assertEquals("item 1", jq("@textbox:eq(1)").val());
		Assertions.assertEquals("item 1", jq(".init-label2").text());
		Assertions.assertEquals("item 1", jq(".bind-label2").text());

		type(jq("@textbox:eq(0)"), "A");
		waitResponse();

		Assertions.assertEquals("item 1", jq(".init-label1").text());
		Assertions.assertEquals("A", jq(".bind-label1").text());
		Assertions.assertEquals("A", jq("@textbox:eq(1)").val());
		Assertions.assertEquals("item 1", jq(".init-label2").text());
		Assertions.assertEquals("A", jq(".bind-label2").text());

		type(jq("@textbox:eq(1)"), "B");
		waitResponse();

		Assertions.assertEquals("B", jq("@textbox:eq(0)").val());
		Assertions.assertEquals("item 1", jq(".init-label1").text());
		Assertions.assertEquals("B", jq(".bind-label1").text());
		Assertions.assertEquals("item 1", jq(".init-label2").text());
		Assertions.assertEquals("B", jq(".bind-label2").text());
	}

	@Test
	public void collectionTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(collection)"));
		waitResponse();

		Assertions.assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());

		click(jq("@listitem:eq(2)"));
		waitResponse();

		Assertions.assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());

		click(jq("@listitem:eq(1)"));
		waitResponse();

		Assertions.assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());
	}
}
