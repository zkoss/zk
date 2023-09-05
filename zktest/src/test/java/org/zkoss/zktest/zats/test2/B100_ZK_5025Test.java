/* B100_ZK_5025Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Sep 08 14:22:26 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;


public class B100_ZK_5025Test extends WebDriverTestCase {
	@Test
	public void test(){
		Actions actions = new Actions(connect());

		JQuery menu1 = jq(".z-menu"),
				menu2 = jq("@menu:eq(1)"),
				menuitem1 = jq("@menuitem:eq(0)"),
				menuitem2 = jq("@menuitem:eq(1)");

		// open menu1
		actions.moveToElement(toElement(menu1));
		click(menu1);
		Assertions.assertTrue(menuitem1.attr("class").equals("z-menuitem"));
		Assertions.assertTrue(menuitem2.attr("class").equals("z-menuitem"));
		Assertions.assertTrue(menu2.attr("class").equals("z-menu"));

		// hover to menuitem1
		actions.moveToElement(toElement(menuitem1)).build().perform();
		Assertions.assertTrue(menuitem1.attr("class").equals("z-menuitem z-menuitem-hover")); // highlight should be here only
		Assertions.assertTrue(menuitem2.attr("class").equals("z-menuitem"));
		Assertions.assertTrue(menu2.attr("class").equals("z-menu"));

		// keydomn:DOWN to menuitem2
		actions.keyDown(Keys.DOWN).perform();
		Assertions.assertTrue(menuitem1.attr("class").equals("z-menuitem"));
		Assertions.assertTrue(menuitem2.attr("class").equals("z-menuitem z-menuitem-focus")); // highlight should be here only
		Assertions.assertTrue(menu2.attr("class").equals("z-menu"));

		// keydomn:DOWN to menu2
		actions.keyDown(Keys.DOWN).perform();
		Assertions.assertTrue(menuitem1.attr("class").equals("z-menuitem"));
		Assertions.assertTrue(menuitem2.attr("class").equals("z-menuitem"));
		Assertions.assertTrue(menu2.attr("class").equals("z-menu z-menu-focus")); // highlight should be here only

		// hover to menuitem2
		actions.moveToElement(toElement(menuitem2)).build().perform();
		Assertions.assertTrue(menuitem1.attr("class").equals("z-menuitem"));
		Assertions.assertTrue(menuitem2.attr("class").equals("z-menuitem z-menuitem-hover")); // highlight should be here only
		Assertions.assertTrue(menu2.attr("class").equals("z-menu"));
	}
}
