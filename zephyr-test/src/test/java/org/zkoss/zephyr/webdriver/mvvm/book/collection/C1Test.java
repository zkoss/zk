/* C1Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 11:52:03 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author rudyhuang
 */
public class C1Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		JQuery listbox = jq("$contentListbox");
		assertEquals(5, listbox.toWidget().nChildren());

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem").eq(1));
		waitResponse();
		assertEquals(9, listbox.toWidget().nChildren());
	}
}
