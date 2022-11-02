/* CollectionsTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 14:53:48 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class MapTest extends ClientBindTestCase {
//	@Test
	public void testRemove() {
		connect("/mvvm/book/viewmodel/data/map.zul");
		click(jq("$gM @row").eq(0).find("@button"));
		waitResponse();
		assertEquals(1, jq("$gM @row").length());
	}

//	@Test
	public void testAdd() {
		connect("/mvvm/book/viewmodel/data/map.zul");
		click(jq("$addM"));
		waitResponse();
		assertEquals(3, jq("$gM @row").length());
	}

//	@Test
	public void testInplaceEdit() {
		connect("/mvvm/book/viewmodel/data/map.zul");
		inplaceEdit(jq("$gM @row").eq(0));
		assertEquals("ZK, 9th Street", getZKLog());
	}

	private void inplaceEdit(JQuery item) {
		JQuery tbs = item.find("@textbox");
		type(tbs.eq(0), "ZK");
		waitResponse();
		type(tbs.eq(1), "9th Street");
		waitResponse();
		click(item.find("@button").eq(1));
		waitResponse();
	}
}
