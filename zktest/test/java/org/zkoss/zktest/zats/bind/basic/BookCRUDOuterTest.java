/* BookCRUDOuterTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 11:31:55 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class BookCRUDOuterTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/bookCrudOuter.zul");

		Assert.assertEquals("include shall work", "ZK MVVM Book CRUD", jq(".z-include .z-window-header").text());
	}
}
