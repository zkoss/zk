/* IfVersusVisibleTest.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 15:50:25 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.advance.specialattributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class IfVersusVisibleTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		JQuery jqVisible = jq("$visible");
		JQuery jqDisabled = jq("$disabled");

		assertEquals(0, jq("$wrong").length());
		assertNotEquals(0, jq("$el").length());
		assertEquals(true, jqVisible.isVisible());
		assertEquals(false, jqDisabled.is(":disabled"));

		click(jq("$checkbox"));
		waitResponse();

		assertEquals(0, jq("$wrong").length());
		assertNotEquals(0, jq("$el").length());
		assertEquals(false, jqVisible.isVisible());
		assertEquals(true, jqDisabled.is(":disabled"));
	}
}
