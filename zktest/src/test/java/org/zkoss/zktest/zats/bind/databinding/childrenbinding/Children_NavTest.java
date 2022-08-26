/* Children_NavTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 16:05:03 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.childrenbinding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Children_NavTest extends WebDriverTestCase {
	@Test
	public void structureTest() {
		connect("/bind/databinding/childrenbinding/children-nav.zul");

		Assertions.assertEquals(0, jq(".z-nav:contains(Item A)").length());
		Assertions.assertEquals(1, jq(".z-navitem:contains(Item A)").length());

		Assertions.assertEquals(1, jq(".z-nav:contains(Item B)").length());
		Assertions.assertEquals(1, jq(".z-navitem:contains(Item B)").length());

		Assertions.assertEquals(2, jq(".z-nav:contains(Item C)").length());
		Assertions.assertEquals(3, jq(".z-navitem:contains(Item C)").length());

		Assertions.assertEquals(7, jq(".z-nav:contains(Item D)").length());
		Assertions.assertEquals(15, jq(".z-navitem:contains(Item D)").length());

		click(jq(".z-nav:contains(Item C)"));
		waitResponse();
		click(jq(".z-nav:contains(Item C_0)"));
		waitResponse();
		click(jq(".z-navitem:contains(Item C_0_1)"));
		waitResponse();
		Assertions.assertEquals("clicked " + jq(".z-navitem:contains(Item C_0_1)").text().trim(), jq("$msg").text());
	}
}
