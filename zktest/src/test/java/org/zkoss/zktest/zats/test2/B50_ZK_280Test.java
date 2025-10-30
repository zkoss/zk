/* B50_ZK_280Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 15:12:47 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_280Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		JQuery menu = jq(".z-menu");
		Assertions.assertFalse(jq(".z-menupopup-open").eq(1).exists());
		
		click(menu);
		waitResponse();
		act.moveToElement(toElement(jq(".z-menupopup-content").eq(0)), 2, 2).build().perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-menupopup-open").eq(1).exists());
		
		click(jq(".z-label"));
		waitResponse();
		
		click(menu);
		waitResponse();
		act.moveToElement(toElement(jq(".z-menupopup-content").eq(0)), 2, 2).build().perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-menupopup-open").eq(1).exists());
	}
}
