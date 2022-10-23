/* ConverterTest.java
	Purpose:

	Description:

	History:
		Mon May 10 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class ChildrenBindingConverterTest extends ZephyrClientMVVMTestCase {

	@Test
	public void test() {
		connect();
		//[Step 1]
		JQuery container1 = jq("$container1");
		JQuery container2 = jq("$container2");
		assertEquals(7, container1.toWidget().nChildren());
		assertEquals(7, container2.toWidget().nChildren());
		JQuery button = jq("@button");
		click(button);
		waitResponse();
		assertEquals(7, container1.toWidget().nChildren());
		assertEquals(8, container2.toWidget().nChildren());
		click(button);
		waitResponse();
		assertEquals(7, container1.toWidget().nChildren());
		assertEquals(9, container2.toWidget().nChildren());
		click(button);
		waitResponse();
		assertEquals(7, container1.toWidget().nChildren());
		assertEquals(10, container2.toWidget().nChildren());
	}
}
