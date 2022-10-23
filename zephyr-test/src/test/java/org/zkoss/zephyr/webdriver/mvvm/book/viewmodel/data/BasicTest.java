/* BasicTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 17:53:16 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author rudyhuang
 */
public class BasicTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/viewmodel/data/basic.zul");
		assertEquals("7", jq("$id").val());
		assertEquals(399.99, Double.parseDouble(jq("$price").val()), 0.01);
		assertEquals("Potix", jq("$name").val());
		assertEquals("NY", jq("$city").val());
		assertEquals("7th Avenue", jq("$street").val());

		type(jq("$id"), "1");
		waitResponse();
		type(jq("$price"), "199.98");
		waitResponse();
		type(jq("$name"), "ZK");
		waitResponse();
		type(jq("$city"), "LA");
		waitResponse();
		type(jq("$street"), "8th Street");
		waitResponse();
		click(jq("$show"));
		waitResponse();
		assertEquals("ID[1] Price[199.98] Name[ZK] Address[LA, 8th Street]", getZKLog());
	}
}
