/* B50_3322970Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 02 15:05:53 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3322970Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		JQuery headerCheckmark = jq(".z-listheader-checkable").eq(0);
		
		click(headerCheckmark);
		waitResponse();
		Assertions.assertTrue(jq(".z-listitem").eq(0).hasClass("z-listitem-selected"));
		Assertions.assertTrue(jq(".z-listitem").eq(1).hasClass("z-listitem-selected"));
		Assertions.assertTrue(jq(".z-listitem").eq(2).hasClass("z-listitem-selected"));
		
		click(jq(".z-listitem-checkable").eq(0));
		waitResponse();
		Assertions.assertFalse(jq(".z-listitem").eq(0).hasClass("z-listitem-selected"));
		
		act.moveToElement(toElement(headerCheckmark), 2, 2).build().perform();
		Assertions.assertFalse(headerCheckmark.hasClass("z-listheader-checked"));
	}
}
