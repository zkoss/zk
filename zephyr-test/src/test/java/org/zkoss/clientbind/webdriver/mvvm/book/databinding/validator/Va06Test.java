/* Va06Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va06Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		JQuery ageBoxAgent = jq("$ageBox");
		JQuery checkBox = jq("$adultBox");
		JQuery checkButton = jq("$checkButton");

		type(ageBoxAgent, "-1");
		waitResponse();
		click(checkButton);
		waitResponse();
		assertFalse(checkBox.hasClass("z-checkbox-on"));

		type(ageBoxAgent, "22");
		waitResponse();
		click(checkButton);
		waitResponse();
		assertTrue(checkBox.hasClass("z-checkbox-on"));

		type(ageBoxAgent, "1");
		waitResponse();
		click(checkButton);
		waitResponse();
		assertFalse(checkBox.hasClass("z-checkbox-on"));
	}
}
