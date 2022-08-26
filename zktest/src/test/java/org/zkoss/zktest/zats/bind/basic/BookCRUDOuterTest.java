/* BookCRUDOuterTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 11:31:55 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class BookCRUDOuterTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/bookCrudOuter.zul");

		Assertions.assertEquals("ZK MVVM Book CRUD", jq(".z-include .z-window-header").text(), "include shall work");
	}
}
