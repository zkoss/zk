/* F86_ZK_4185Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 08 16:30:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F86_ZK_4185Test extends WebDriverTestCase {
	private JQuery comboboxReal = jq("@combobox > input");

	@Test
	public void test() {
		connect();

		testInstantSelectOn();

		click(jq("@button"));
		waitResponse();

		closeZKLog();
		testInstantSelectOff();

		closeZKLog();
		testInstantSelectOffCancel();
	}

	private void testInstantSelectOn() {
		click(jq("@combobox > a"));
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		Assertions.assertEquals(6, getZKLog().split("\n").length); // 3x onChange + onSelect
	}

	private void testInstantSelectOff() {
		click(jq("@combobox > a"));
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ENTER);
		waitResponse();
		Assertions.assertEquals(2, getZKLog().split("\n").length); // onChange + onSelect
	}

	private void testInstantSelectOffCancel() {
		String origVal = comboboxReal.val();
		click(jq("@combobox > a"));
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ARROW_DOWN);
		waitResponse();
		sendKeys(comboboxReal, Keys.ESCAPE);
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable(), "Shouldn't have any event");
		Assertions.assertEquals(origVal, comboboxReal.val());

		click(jq("@button:contains(Show selected)"));
		waitResponse();
		Assertions.assertEquals(origVal, getZKLog());
	}
}
