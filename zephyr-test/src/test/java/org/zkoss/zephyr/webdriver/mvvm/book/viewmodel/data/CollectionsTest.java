/* CollectionsTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 14:53:48 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class CollectionsTest extends WebDriverTestCase {
	@Test
	public void testRemove() {
		connect("/mvvm/book/viewmodel/data/collections.zul");
		click(jq("$gL @row").eq(0).find("@button"));
		waitResponse();
		click(jq("$gS @row").eq(0).find("@button"));
		waitResponse();
		assertEquals(1, jq("$gL @row").length());
		assertEquals(1, jq("$gS @row").length());
	}

	@Test
	public void testAdd() {
		connect("/mvvm/book/viewmodel/data/collections.zul");
		click(jq("$addL"));
		waitResponse();
		click(jq("$addS"));
		waitResponse();
		assertEquals(3, jq("$gL @row").length());
		assertEquals(3, jq("$gS @row").length());
	}

	@Test
	public void testInplaceEdit() {
		connect("/mvvm/book/viewmodel/data/collections.zul");
		inplaceEdit(jq("$gL @row").eq(0));
		inplaceEdit(jq("$gS @row").eq(0));
		assertEquals("ZK, 9th Street\nZK, 9th Street", getZKLog());
	}

	private void inplaceEdit(JQuery item) {
		JQuery tbs = item.find("@textbox");
		toElement(tbs.eq(0)).clear();
		waitResponse();
		click(tbs.eq(0));
		waitResponse();
		sendKeys(tbs.eq(0), "ZK");
		waitResponse();
		blur(tbs.eq(0));
		toElement(tbs.eq(1)).clear();
		waitResponse();
		click(tbs.eq(1));
		waitResponse();
		sendKeys(tbs.eq(1), "9th Street");
		waitResponse();
		blur(tbs.eq(1));
		waitResponse();
		click(item.find("@button").eq(1));
		waitResponse();
	}
}
