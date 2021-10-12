/* F86_ZK_4185Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 08 16:30:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertEquals(6, getZKLog().split("\n").length); // 3x onChange + onSelect
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
		Assert.assertEquals(2, getZKLog().split("\n").length); // onChange + onSelect
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
		Assert.assertFalse("Shouldn't have any event", isZKLogAvailable());
		Assert.assertEquals(origVal, comboboxReal.val());

		click(jq("@button:contains(Show selected)"));
		waitResponse();
		Assert.assertEquals(origVal, getZKLog());
	}
}
