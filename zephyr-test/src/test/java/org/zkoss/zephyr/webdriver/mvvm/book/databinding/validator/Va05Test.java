/* Va05Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va05Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String UNDER_AGE = "Under Age";
		String OVER_AGE = "Over Age";

		JQuery ageBox = jq("$ageBox");
		JQuery submitButton = jq("$submitButton");
		JQuery limitBox = jq("$limitBox");
		JQuery messageLabel = jq("$messageLabel");

		type(ageBox, "-1");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertEquals(UNDER_AGE + " " + limitBox.val(), messageLabel.text());

		type(ageBox, "11");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertEquals(OVER_AGE + " " + limitBox.val(), messageLabel.text());

		clickAt(limitBox, 3, 3);
		waitResponse();
		sendKeys(limitBox, Keys.HOME, Keys.DELETE, Keys.DELETE, "20");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertEquals(UNDER_AGE + " " + limitBox.val(), messageLabel.text());

		type(ageBox, "22");
		waitResponse();
		click(submitButton);
		waitResponse();
		assertEquals(OVER_AGE + " " + limitBox.val(), messageLabel.text());
	}
}
