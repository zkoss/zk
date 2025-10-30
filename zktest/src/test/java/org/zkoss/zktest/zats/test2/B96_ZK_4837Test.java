/* B96_ZK_4837Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 09 17:30:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_4837Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		//test button
		JQuery cave = jq("$sbox1").find(".z-searchbox-cave");
		assertEquals(0, cave.children().length());
		JQuery jqBtns = jq("@button");
		click(jqBtns.eq(0));
		waitResponse();
		assertEquals(2, cave.children().length());
		click(jqBtns.eq(1));
		waitResponse();
		assertEquals(4, cave.children().length());
		click(jqBtns.eq(2));
		waitResponse();
		assertEquals(5, cave.children().length());
		assertEquals("new item", cave.find("li").eq(1).text());

		//test onOpen
		JQuery jqSbox2 = jq("$sbox2");
		cave = jq(jqSbox2.toWidget().$n("pp")).find(".z-searchbox-cave");
		assertEquals(0, cave.children().length());
		click(jqSbox2);
		waitResponse();
		assertEquals(2, cave.children().length());
		click(jqSbox2);
		click(jqSbox2);
		waitResponse();
		assertEquals(4, cave.children().length());
		click(jqSbox2);
		click(jqSbox2);
		waitResponse();
		assertEquals(5, cave.children().length());
		assertEquals("new item", cave.find("li").eq(1).text());
		click(jqSbox2); //close
		waitResponse();

		//test onSearching
		JQuery jqSbox3 = jq("$sbox3");
		cave = jq(jqSbox3.toWidget().$n("pp")).find(".z-searchbox-cave");
		assertEquals(0, cave.children().length());
		click(jqSbox3);
		waitResponse();
		JQuery jqSearchInput = cave.parent().find(".z-searchbox-search");
		sendKeys(jqSearchInput, "a");
		waitResponse();
		assertEquals(2, cave.children().length());
		sendKeys(jqSearchInput, Keys.BACK_SPACE);
		waitResponse();
		assertEquals(4, cave.children().length());
		sendKeys(jqSearchInput, "b");
		waitResponse();
		sendKeys(jqSearchInput, Keys.BACK_SPACE);
		waitResponse();
		assertEquals(6, cave.children().length());
		assertEquals("new item", cave.find("li").eq(1).text());
	}
}
