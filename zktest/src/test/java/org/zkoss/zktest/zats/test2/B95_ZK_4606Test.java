/* B95_ZK_4606Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Aug 18 16:42:27 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_4606Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		JQuery headerCheckbox = jq(".z-listheader-checkable");
		JQuery listItemOne = jq(".z-listitem").eq(0);
		JQuery listItemTwo = jq(".z-listitem").eq(1);
		JQuery listItemThree = jq(".z-listitem").eq(2);
		
		click(headerCheckbox);
		waitResponse();
		
		Assertions.assertTrue(headerCheckbox.hasClass("z-listheader-checked"));
		Assertions.assertTrue(listItemOne.hasClass("z-listitem-selected"));
		Assertions.assertTrue(listItemTwo.hasClass("z-listitem-selected"));
		Assertions.assertTrue(listItemThree.hasClass("z-listitem-selected"));
		
		click(listItemOne);
		waitResponse();
		
		Assertions.assertFalse(headerCheckbox.hasClass("z-listheader-checked"));
		Assertions.assertFalse(listItemOne.hasClass("z-listitem-selected"));
		Assertions.assertTrue(listItemTwo.hasClass("z-listitem-selected"));
		Assertions.assertTrue(listItemThree.hasClass("z-listitem-selected"));
		
		click(headerCheckbox);
		waitResponse();
		
		Assertions.assertTrue(headerCheckbox.hasClass("z-listheader-checked"));
		Assertions.assertTrue(listItemOne.hasClass("z-listitem-selected"));
		Assertions.assertTrue(listItemTwo.hasClass("z-listitem-selected"));
		Assertions.assertTrue(listItemThree.hasClass("z-listitem-selected"));
	}
}
