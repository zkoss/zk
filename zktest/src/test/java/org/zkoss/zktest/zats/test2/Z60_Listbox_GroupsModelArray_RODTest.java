/* Z60_Listbox_GroupsModelArrayTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 23 09:45:33 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class Z60_Listbox_GroupsModelArray_RODTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(widget("@listbox:eq(0) @listgroup:first").$n("img"));
		waitResponse();
		Assert.assertFalse(jq("@listbox:eq(0) @listgroup:first").hasClass("z-listgroup-open"));
		Assert.assertFalse(jq("@listbox:eq(1) @listgroup:first").hasClass("z-listgroup-open"));

		click(widget("@listbox:eq(1) @listgroup:first").$n("img"));
		waitResponse();
		Assert.assertTrue(jq("@listbox:eq(0) @listgroup:first").hasClass("z-listgroup-open"));
		Assert.assertTrue(jq("@listbox:eq(1) @listgroup:first").hasClass("z-listgroup-open"));

		click(jq("@listbox:eq(0) @listheader:eq(1)"));
		waitResponse();
		click(jq("@listbox:eq(0) @listheader:eq(1)"));
		waitResponse();
		Assert.assertEquals("Value 2", jq("@listbox:eq(1) @listitem:eq(0) @listcell:eq(1)").text());
		Assert.assertEquals("Value 1", jq("@listbox:eq(1) @listitem:eq(1) @listcell:eq(1)").text());


	}

	@Test
	public void testClone() {
		connect();

		scrollToBottom(jq("@listbox:eq(2)"));
		click(widget("@listbox:eq(2) @listgroup:last").$n("img"));
		waitResponse();
		Assert.assertFalse(jq("@listbox:eq(2) @listgroup:last").hasClass("z-listgroup-open"));

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();

		JQuery cloned1 = jq("$cloneThreeArea @listbox:eq(0)");
		JQuery cloned2 = jq("$cloneThreeArea @listbox:eq(1)");
		scrollToBottom(cloned1);
		scrollToBottom(cloned2);
		Assert.assertFalse(cloned1.find("@listgroup:last").hasClass("z-listgroup-open"));
		Assert.assertFalse(cloned2.find("@listgroup:last").hasClass("z-listgroup-open"));

		click(widget("@listbox:eq(2) @listgroup:last").$n("img"));
		waitResponse();
		Assert.assertFalse(cloned1.find("@listgroup:last").hasClass("z-listgroup-open"));
		Assert.assertFalse(cloned2.find("@listgroup:last").hasClass("z-listgroup-open"));
	}

	private void scrollToBottom(JQuery listbox) {
		JQuery body = listbox.find(".z-listbox-body");
		body.scrollTop(body.scrollHeight());
		waitResponse();
	}

	@Test
	public void testClone2() {
		connect();

		click(widget("@listbox:eq(2) @listgroup:eq(1)").$n("img"));
		waitResponse();
		scrollToBottom(jq("@listbox:eq(2)"));
		click(widget("@listbox:eq(2) @listgroup:last").$n("img"));
		waitResponse();

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();

		JQuery cloned1 = jq("$cloneThreeArea @listbox:eq(0)");
		JQuery cloned2 = jq("$cloneThreeArea @listbox:eq(1)");
		cloned1.scrollTop(0);
		cloned2.scrollTop(0);
		waitResponse();

		click(widget(cloned1.find("@listgroup:eq(1)")).$n("img"));
		waitResponse();
		click(widget(cloned2.find("@listgroup:eq(1)")).$n("img"));
		waitResponse();
		Assert.assertTrue(cloned1.find("@listgroup:eq(1)").hasClass("z-listgroup-open"));
		Assert.assertTrue(cloned2.find("@listgroup:eq(1)").hasClass("z-listgroup-open"));

		scrollToBottom(cloned1);
		scrollToBottom(cloned2);
		Assert.assertFalse(cloned1.find("@listgroup:last").hasClass("z-listgroup-open"));
		Assert.assertFalse(cloned2.find("@listgroup:last").hasClass("z-listgroup-open"));
	}
}
