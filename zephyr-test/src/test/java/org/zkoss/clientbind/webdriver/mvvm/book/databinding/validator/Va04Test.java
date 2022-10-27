/* Va03Test.java

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

/**
 * @author jameschu
 */
public class Va04Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		String ADULT = "Adult";

		JQuery ageBoxAgent = jq("$ageBox");
		JQuery addButton = jq("$addButton");
		JQuery afterAge = jq("$afterAge");
		JQuery originalAge = jq("$originalAge");
		JQuery adultLabel = jq("$adultLabel");

		type(ageBoxAgent, "1");
		waitResponse();
		click(addButton);
		waitResponse();
		assertEquals("1", originalAge.text());
		assertEquals("11", afterAge.text());

		type(ageBoxAgent, "22");
		waitResponse();
		click(addButton);
		waitResponse();
		assertEquals(ADULT, adultLabel.text());

		type(ageBoxAgent, "33");
		waitResponse();
		click(addButton);
		waitResponse();
		assertEquals(ADULT, adultLabel.text());
	}
}
