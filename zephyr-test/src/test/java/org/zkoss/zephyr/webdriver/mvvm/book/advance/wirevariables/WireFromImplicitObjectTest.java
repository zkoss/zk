/* WireFromImplicitObjectTest.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 17:35:46 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.wirevariables;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class WireFromImplicitObjectTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNotEquals("", jq("$page1").text());
		assertNotEquals("", jq("$desktop1").text());
		assertNotEquals("", jq("$sess").text());
		assertNotEquals("", jq("$wapp").text());
	}
}
