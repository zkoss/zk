/* F86_ZK_4059Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 20 11:07:46 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F86_ZK_4059Test extends WebDriverTestCase {
	@Test
	public void testListhead() {
		connect();
		Actions act = new Actions(driver);
		
		act.moveToElement(toElement(jq("$lh1"))).click(toElement(widget("$lh1").$n("btn"))).build().perform();
		Assertions.assertTrue(jq(".z-menupopup").isVisible());

		act.moveToElement(toElement(jq(".z-menuitem-checkable:visible:eq(0)"))).click().build().perform();
		Assertions.assertTrue(jq(".z-menupopup").isVisible());

		act.moveToElement(toElement(jq("$lh2"))).click(toElement(widget("$lh2").$n("btn"))).build().perform();
		Assertions.assertTrue(jq(".z-menupopup").isVisible());

		act.moveToElement(toElement(jq(".z-menuitem-checkable:visible:eq(0)"))).click().build().perform();
		Assertions.assertFalse(jq(".z-menupopup").isVisible());
	}

	@Test
	public void testColumns() {
		connect();
		Actions act = new Actions(driver);
		
		act.moveToElement(toElement(jq("$col1"))).click(toElement(widget("$col1").$n("btn"))).build().perform();
		Assertions.assertTrue(jq(".z-menupopup").isVisible());
		
		act.moveToElement(toElement(jq(".z-menuitem-checkable:visible:eq(0)"))).click().build().perform();
		Assertions.assertTrue(jq(".z-menupopup").isVisible());
		
		act.moveToElement(toElement(jq("$col2"))).click(toElement(widget("$col2").$n("btn"))).build().perform();
		Assertions.assertTrue(jq(".z-menupopup").isVisible());
		
		act.moveToElement(toElement(jq(".z-menuitem-checkable:visible:eq(0)"))).click().build().perform();
		Assertions.assertFalse(jq(".z-menupopup").isVisible());
	}
}
