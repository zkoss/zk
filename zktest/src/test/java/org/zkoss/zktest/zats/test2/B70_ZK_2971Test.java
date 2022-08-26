/* B70_ZK_2971Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 11:40:17 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2971Test extends WebDriverTestCase {
	@Test
	public void testSingleSelection() {
		connect();

		testSingleSelection0(jq("@listbox:eq(0)"));
		testSingleSelection0(jq("@listbox:eq(1)"));
		testSingleSelection0(jq("@listbox:eq(2)"));
		testSingleSelection0(jq("@listbox:eq(3)"));
	}

	private void testSingleSelection0(JQuery lb) {
		click(lb.find("@listitem:contains(Item 6)"));
		for (int i = 0; i < 13; i++) {
			sendKeys(lb.find(".z-focus-a"), Keys.DOWN);
		}
		waitResponse();
		JQuery body = lb.find(".z-listbox-body");
		Assertions.assertEquals(body.scrollHeight() - body.height(), body.scrollTop(), 1);

		sendKeys(lb.find(".z-focus-a"), Keys.DOWN);
		waitResponse();
		Assertions.assertEquals(0, body.scrollTop());
	}

	@Test
	public void testMultipleSelection() {
		connect();

		testMultipleSelection0(jq("@listbox:eq(1)"));
		testMultipleSelection0(jq("@listbox:eq(3)"));
	}

	private void testMultipleSelection0(JQuery lb) {
		click(lb.find("@listitem:contains(Item 6)"));
		JQuery body = lb.find(".z-listbox-body");
		int scrollTop = body.scrollTop();

		sendKeys(lb.find(".z-focus-a"), Keys.chord(Keys.SHIFT, Keys.DOWN));
		sendKeys(lb.find(".z-focus-a"), Keys.chord(Keys.SHIFT, Keys.DOWN));
		waitResponse();
		Assertions.assertTrue(body.scrollTop() > scrollTop);
	}

	@Test
	public void testSetSelectedIndexApi() {
		connect();

		type(jq("@intbox"), "76");
		waitResponse();
		click(jq("@button"));
		waitResponse();

		testItemInViewport(jq("@listbox:eq(0)"), jq("@listbox:eq(0) @listitem:contains(Item 76)"));
		testItemInViewport(jq("@listbox:eq(1)"), jq("@listbox:eq(1) @listitem:contains(Item 76)"));
		testItemInViewport(jq("@listbox:eq(2)"), jq("@listbox:eq(2) @listitem:contains(Item 76)"));
		testItemInViewport(jq("@listbox:eq(3)"), jq("@listbox:eq(3) @listitem:contains(Item 76)"));
	}

	private void testItemInViewport(JQuery lb, JQuery item) {
		JQuery jqBody = lb.find(".z-listbox-body");
		int itemTop = item.positionTop();
		Assertions.assertTrue(itemTop - jqBody.scrollTop() >= 0);
		Assertions.assertTrue(itemTop - jqBody.scrollTop() < jqBody.height());
	}
}
