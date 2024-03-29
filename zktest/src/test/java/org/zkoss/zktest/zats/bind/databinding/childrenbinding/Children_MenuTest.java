/* Children_MenuTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 16:36:34 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.childrenbinding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Children_MenuTest extends WebDriverTestCase {
	@Test
	public void structureTest() {
		connect("/bind/databinding/childrenbinding/children-menu.zul");

		Assertions.assertEquals(0, jq(".z-menu:contains(Item A)").length());
		Assertions.assertEquals(1, jq(".z-menuitem:contains(Item A)").length());

		// click to open popup make sure jq could find element
		click(jq(".z-menu:contains(Item B)"));
		waitResponse();
		Assertions.assertEquals(1, jq(".z-menu:contains(Item B)").length());
		Assertions.assertEquals(1, jq(".z-menuitem:contains(Item B)").length());
		click(jq(".z-window")); // close popup
		waitResponse();

		// click to open popup make sure jq could find element
		click(jq(".z-menu:contains(Item C)"));
		waitResponse();
		click(jq(".z-menu:contains(Item C_0)"));
		waitResponse();
		Assertions.assertEquals(2, jq(".z-menu:contains(Item C)").length());
		Assertions.assertEquals(3, jq(".z-menuitem:contains(Item C)").length());
		click(jq(".z-window")); // close popup
		waitResponse();

		// click to open popup make sure jq could find element
		click(jq(".z-menu:contains(Item D)"));
		waitResponse();
		click(jq(".z-menu:contains(Item D_0)"));
		waitResponse();
		click(jq(".z-menu:contains(Item D_0_0)"));
		waitResponse();
		click(jq(".z-menu:contains(Item D_0_1)"));
		waitResponse();
		click(jq(".z-menu:contains(Item D_1)"));
		waitResponse();
		click(jq(".z-menu:contains(Item D_1_0)"));
		waitResponse();
		click(jq(".z-menu:contains(Item D_1_1)"));
		waitResponse();
		Assertions.assertEquals(7, jq(".z-menu:contains(Item D)").length());
		Assertions.assertEquals(15, jq(".z-menuitem:contains(Item D)").length());
		click(jq(".z-window")); // close popup
		waitResponse();

		click(jq(".z-menu:contains(Item C)"));
		waitResponse();
		click(jq(".z-menu:contains(Item C_0)"));
		waitResponse();
		click(jq(".z-menuitem:contains(Item C_0_1)"));
		waitResponse();
		Assertions.assertEquals("clicked " + jq(".z-menuitem:contains(Item C_0_1)").text().trim(), jq("$msg").text());
	}
}