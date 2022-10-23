/* SelectboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 16:38:35 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class SelectboxTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		final Select select = new Select(toElement(jq("@selectbox")));
		final JQuery $selected = jq("$selected");

		select.selectByVisibleText("item04");
		waitResponse();
		assertEquals("3", $selected.text());

		select.selectByVisibleText("item02");
		waitResponse();
		assertEquals("1", $selected.text());
	}
}
