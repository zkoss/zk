/* Va02Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va02Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		String UNDER_AGE = "Under Age";
		String ADULT = "Adult";

		JQuery ageBox = jq("$ageBox");
		JQuery addButton = jq("$addButton");
		JQuery adultLabel = jq("$adultLabel");
		JQuery beforeAge = jq("$beforeAge");
		JQuery ageLabel = jq("$ageLabel");

		assertEquals("-1", ageBox.val());
		click(addButton);
		waitResponse();
		assertEquals(UNDER_AGE, adultLabel.text());

		type(ageBox, "1");
		click(addButton);
		waitResponse();
		assertEquals("1", beforeAge.text());
		assertEquals("11", ageLabel.text());
		assertEquals(UNDER_AGE, adultLabel.text());

		type(ageBox, "18");
		click(addButton);
		waitResponse();
		assertEquals(ADULT, adultLabel.text());
	}
}
