/* B80_ZK_2937Test.java

	Purpose:
		
	Description:
		
	History:
		11:56 AM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B80_ZK_2937Test extends WebDriverTestCase {
	@Test public void testZK2937() {
		connect();
		JQuery jq = jq("@combobox");
		JQuery inp = jq.find(".z-combobox-input");
		sendKeys(inp, "ar_AE");
		waitResponse();
		assertTrue(jq("@comboitem").exists());
		click(jq(".z-comboitem"));
		waitResponse();
		assertEquals("ar_AE", inp.val());
		getWebDriver().findElement(By.className("z-combobox-input")).sendKeys(
				"\b\b"); // backspace key twice
		assertEquals("ar_", inp.val());
		waitResponse();
		assertTrue(jq(".z-comboitem-selected").exists());
		assertEquals("ar_AE", jq(".z-comboitem-selected").text());
	}
}
