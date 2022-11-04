/* Va03Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va03Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String UNDER_AGE = "Under Age";
		String ADULT = "Adult";

		JQuery ageBoxAgent = jq("$ageBox");
		JQuery minusButton = jq("$minusButton");
		JQuery adultLabel = jq("$adultLabel");
		JQuery ageLabel = jq("$ageLabel");

		type(ageBoxAgent, "1");
		waitResponse();
		click(minusButton);
		waitResponse();
		waitResponse();
		assertEquals(UNDER_AGE, adultLabel.text());

		type(ageBoxAgent, "28");
		waitResponse();
		click(minusButton);
		waitResponse();
		assertEquals("18", ageLabel.text());
		assertEquals(ADULT, adultLabel.text());
	}
}
