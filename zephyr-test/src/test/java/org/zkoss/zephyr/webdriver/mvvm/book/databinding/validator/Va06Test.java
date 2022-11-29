/* Va06Test.java

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
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va06Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery ageBoxAgent = jq("$ageBox");
		JQuery checkBox = jq("$adultBox");
		JQuery checkButton = jq("$checkButton");

		sendKeys(ageBoxAgent, Keys.END, Keys.BACK_SPACE, Keys.BACK_SPACE, "-1");
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
