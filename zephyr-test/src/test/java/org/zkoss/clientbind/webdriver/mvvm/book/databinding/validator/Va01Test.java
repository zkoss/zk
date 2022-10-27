/* Va01Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Va01Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		String UNDER_AGE = "Under Age";
		String ADULT = "Adult";

		JQuery ageBox = jq("$ageBox");
		JQuery submitButton = jq("$submitButton");
		JQuery adultLabel = jq("$adultLabel");

		assertEquals("-1", ageBox.val());

		type(ageBox, "22");
		click(submitButton);
		waitResponse();
		assertEquals(ADULT, adultLabel.text());

		type(ageBox, "-1");
		click(submitButton);
		waitResponse();
		assertEquals(ADULT, adultLabel.text());

		type(ageBox, "11");
		click(submitButton);
		waitResponse();
		assertEquals(UNDER_AGE, adultLabel.text());
	}
}
