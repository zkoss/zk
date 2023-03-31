/* BookCRUDOuterTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 11:31:55 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class BookCRUDOuterTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/bookCrudOuter.zul");
		waitResponse();

		assertEquals("ZK MVVM Book CRUD", jq(".z-include .z-window-header").text(), "include shall work");
	}
}
