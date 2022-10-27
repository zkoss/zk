/* Va11Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va11Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		JQuery ageBoxAgent = jq("$ageBox");
		JQuery submitButton = jq("$submitButton");
		JQuery less13 = jq("$less13");
		JQuery less18 = jq("$less18");
		JQuery over18 = jq("$over18");

		type(ageBoxAgent, "-1");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertTrue(less13.isVisible());
		assertTrue(less18.isVisible());
		assertFalse(over18.isVisible());

		type(ageBoxAgent, "1");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertTrue(less13.isVisible());
		assertTrue(less18.isVisible());
		assertFalse(over18.isVisible());

		type(ageBoxAgent, "15");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertFalse(less13.isVisible());
		assertTrue(less18.isVisible());
		assertFalse(over18.isVisible());

		type(ageBoxAgent, "18");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertFalse(less13.isVisible());
		assertFalse(less18.isVisible());
		assertTrue(over18.isVisible());
	}
}
