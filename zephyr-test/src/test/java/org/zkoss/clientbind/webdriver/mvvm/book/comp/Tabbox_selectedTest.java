/* Tabbox_selectedTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 18:08:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class Tabbox_selectedTest extends ClientBindTestCase {
	@Test
	public void testSingleWay() {
		connect();
		JQuery listitems = jq("$listbox1 @listitem");
		JQuery tabbox = jq("$tabbox1");
		click(listitems.eq(0));
		waitResponse();
		assertTrue(tabbox.find("@tab").eq(0).hasClass("z-tab-selected"));
		click(listitems.eq(1));
		waitResponse();
		assertTrue(tabbox.find("@tab").eq(1).hasClass("z-tab-selected"));
		click(jq("$tabbox1 @tab").eq(2));
		assertTrue(listitems.eq(1).hasClass("z-listitem-selected")); // Won't change
	}

	@Test
	public void testTwoWay() {
		connect();
		JQuery listitems = jq("$listbox2 @listitem");
		JQuery tabbox = jq("$tabbox2");
		click(listitems.eq(0));
		waitResponse();
		assertTrue(tabbox.find("@tab").eq(0).hasClass("z-tab-selected"));
		click(listitems.eq(1));
		waitResponse();
		assertTrue(tabbox.find("@tab").eq(1).hasClass("z-tab-selected"));
		click(tabbox.find("@tab").eq(2));
		assertTrue(listitems.eq(2).hasClass("z-listitem-selected"));
	}
}
