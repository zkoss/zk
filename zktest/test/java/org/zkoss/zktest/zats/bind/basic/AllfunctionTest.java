/* AllfunctionTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 26 11:52:28 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class AllfunctionTest extends WebDriverTestCase {
	@Test
	public void warmTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(warn)"));
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		assertNoJSError();
		Assert.assertFalse(hasError());
	}
	@Test
	public void basicTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(basic)"));
		waitResponse();

		Assert.assertEquals("init binding should work", "item 1", jq(".init-label").text());
		Assert.assertEquals("init binding should work", "'", jq(".esc1").text());
		Assert.assertEquals("init binding should work", "\"", jq(".esc2").text());

		type(jq("@textbox:eq(1)"), "B");
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		Assert.assertEquals("value must be 'A'", jq(".z-notification-content").text());

		click(jq(".z-label"));
		waitResponse();

		click(jq("@button:contains(save2)"));
		waitResponse();

		Assert.assertEquals("value must be 'A'", jq(".z-notification-content").text());

		type(jq("@textbox:eq(1)"), "A");
		waitResponse();

		click(jq("@button:contains(save1)"));
		waitResponse();

		Assert.assertEquals("A-GCMD1", jq("@textbox:eq(0)").val());
		Assert.assertEquals("A-GCMD1", jq(".bind-label").text());
		Assert.assertEquals("A", jq(".load-label").text());

		click(jq("@button:contains(save2)"));
		waitResponse();

		Assert.assertEquals("A-GCMD2", jq("@textbox:eq(0)").val());
		Assert.assertEquals("A-GCMD2", jq(".bind-label").text());
		Assert.assertEquals("A", jq(".load-label").text());
	}

	@Test
	public void formTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(form)"));
		waitResponse();

		Assert.assertEquals("item 1", jq("@textbox").val());
		Assert.assertEquals("item 1", jq(".load-label1").text());
		Assert.assertEquals("item 1", jq(".load-label2").text());

		type(jq("@textbox"), "A");
		waitResponse();

		Assert.assertEquals("A", jq(".load-label1").text());
		Assert.assertEquals("item 1", jq(".load-label2").text());

		click(jq("@button:contains(save3)"));
		waitResponse();

		Assert.assertEquals("value must be 'B'", jq(".z-notification-content").text());

		type(jq("@textbox"), "B");
		waitResponse();

		Assert.assertEquals("B", jq(".load-label1").text());
		Assert.assertEquals("item 1", jq(".load-label2").text());

		click(jq("@button:contains(save3)"));
		waitResponse();

		Assert.assertEquals("B", jq(".load-label1").text());
		Assert.assertEquals("B", jq(".load-label2").text());
	}

	@Test
	public void referenceTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(reference)"));
		waitResponse();

		Assert.assertEquals("item 1", jq("@textbox:eq(0)").val());
		Assert.assertEquals("item 1", jq(".init-label1").text());
		Assert.assertEquals("item 1", jq(".bind-label1").text());

		Assert.assertEquals("item 1", jq("@textbox:eq(1)").val());
		Assert.assertEquals("item 1", jq(".init-label2").text());
		Assert.assertEquals("item 1", jq(".bind-label2").text());

		type(jq("@textbox:eq(0)"), "A");
		waitResponse();

		Assert.assertEquals("item 1", jq(".init-label1").text());
		Assert.assertEquals("A", jq(".bind-label1").text());
		Assert.assertEquals("A", jq("@textbox:eq(1)").val());
		Assert.assertEquals("item 1", jq(".init-label2").text());
		Assert.assertEquals("A", jq(".bind-label2").text());

		type(jq("@textbox:eq(1)"), "B");
		waitResponse();

		Assert.assertEquals("B", jq("@textbox:eq(0)").val());
		Assert.assertEquals("item 1", jq(".init-label1").text());
		Assert.assertEquals("B", jq(".bind-label1").text());
		Assert.assertEquals("item 1", jq(".init-label2").text());
		Assert.assertEquals("B", jq(".bind-label2").text());
	}

	@Test
	public void collectionTest() {
		connect("/bind/basic/allfunction.zul");

		click(jq("@button:contains(collection)"));
		waitResponse();

		Assert.assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());

		click(jq("@listitem:eq(2)"));
		waitResponse();

		Assert.assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());

		click(jq("@listitem:eq(1)"));
		waitResponse();

		Assert.assertEquals(jq(".z-listitem-selected").text(), jq(".bind-label").text());
	}
}
