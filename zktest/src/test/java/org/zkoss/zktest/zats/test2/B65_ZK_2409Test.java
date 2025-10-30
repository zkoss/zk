/* B65_ZK_2409Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 09 10:58:19 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThan;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B65_ZK_2409Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		JQuery bottomCombobox = jq("@combobox:eq(1)");
		click(bottomCombobox);
		waitResponse();
		act.sendKeys(Keys.NUMPAD0).perform();
		waitResponse();
		JQuery comboboxPopup = jq(".z-combobox-popup.z-combobox-open");
		MatcherAssert.assertThat("the popup should appear on top of the combobox",
			comboboxPopup.offsetTop() + comboboxPopup.height(), lessThan(bottomCombobox.offsetTop()));

		act.sendKeys(Keys.NUMPAD0).perform();
		waitResponse();
		Assertions.assertEquals(1, jq(".z-comboitem-selected").length(), "the popup should only have '00' left and appear selected");
		Assertions.assertTrue(jq(".z-comboitem-selected").text().contains("00"), "the popup should only have '00' left and appear selected");

		act.sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assertions.assertFalse(comboboxPopup.exists());

		click(bottomCombobox);
		waitResponse();
		act.sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		MatcherAssert.assertThat("it is bug if popup do not appear on top of the combobox",
			comboboxPopup.offsetTop() + comboboxPopup.height(), lessThan(bottomCombobox.offsetTop()));
	}
}
