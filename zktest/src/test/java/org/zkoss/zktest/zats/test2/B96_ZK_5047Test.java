/* B96_ZK_5047Test.java

	Purpose:
		
	Description:
		
	History:
		12:03 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5047Test  extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		act.sendKeys(toElement(jq(".z-combobox-input")), "eng").perform();
		waitResponse();
		act.sendKeys(Keys.ENTER).perform();
		waitResponse();

		assertEquals("SelectedItem   : english (niue)\n" + "SelectedObject : english (niue)", getZKLog());
	}
}
