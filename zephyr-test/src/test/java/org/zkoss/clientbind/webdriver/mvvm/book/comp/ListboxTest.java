/* ListboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:28:08 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class ListboxTest extends ClientBindTestCase {
	@Test
	public void testListgroup() {
		connect();
		click(jq("@listgroup .z-listgroup-icon"));
		waitResponse();
		assertEquals("true", jq("$open").text());
		click(jq("@listgroup .z-listgroup-icon"));
		waitResponse();
		assertEquals("false", jq("$open").text());
	}

	@Test
	public void testSelected() {
		connect();
		click(jq("$listbox > @listitem").eq(0));
		waitResponse();
		assertEquals("item01", jq("$itemLabel").text());
		assertEquals("0", jq("$indexLabel").text());
		click(jq("$listbox > @listitem").eq(3));
		waitResponse();
		assertEquals("false", jq("$open").text());
		assertEquals("item04", jq("$itemLabel").text());
		assertEquals("3", jq("$indexLabel").text());
	}
}
