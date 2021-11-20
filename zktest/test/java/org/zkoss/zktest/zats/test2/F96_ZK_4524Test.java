/* F96_ZK_4524Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Feb 19 15:37:04 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

@Category(ForkJVMTestOnly.class)
public class F96_ZK_4524Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-4524-zk.xml");

	@Test
	public void testProcessMask() {
		connect();
		JQuery textbox1 = jq("@textbox:eq(0)");
		Actions actions = getActions();
		click(jq("@button:eq(0)"));
		waitResponse();
		actions.sendKeys(Keys.TAB, "browser showBusy").perform();
		Assert.assertEquals("", textbox1.val());
		sleep(3000);
		sendKeys(textbox1, "browser showBusy");
		Assert.assertEquals("browser showBusy", textbox1.val());
	}
	@Test
	public void testBrowserShowBusy() {
		connect();
		Actions actions = getActions();
		JQuery textbox1 = jq("@textbox:eq(0)");
		click(jq("@button:eq(1)"));
		waitResponse();
		actions.sendKeys(Keys.TAB, "browser showBusy").perform();
		Assert.assertEquals("", textbox1.val());
		sleep(2000);
		sendKeys(textbox1, "browser showBusy");
		Assert.assertEquals("browser showBusy", textbox1.val());
	}
}
