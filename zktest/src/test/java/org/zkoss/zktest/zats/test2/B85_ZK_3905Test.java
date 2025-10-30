/* B85_ZK_3905Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 30 12:25:29 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3905Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		JQuery headerCheckbox = jq(".z-listheader-checkable");
		JQuery listItemOne = jq(".z-listitem").eq(0);
		JQuery listItemTwo = jq(".z-listitem").eq(1);
		
		click(headerCheckbox);
		waitResponse();
		
		Assertions.assertTrue(headerCheckbox.hasClass("z-listheader-checked"));
		Assertions.assertTrue(listItemOne.hasClass("z-listitem-selected"));
		Assertions.assertTrue(listItemTwo.hasClass("z-listitem-selected"));
		
		click(jq(".z-button"));
		waitResponse();
		
		Assertions.assertFalse(headerCheckbox.hasClass("z-listheader-checked"));
		Assertions.assertFalse(listItemOne.hasClass("z-listitem-selected"));
		Assertions.assertFalse(listItemTwo.hasClass("z-listitem-selected"));
		
		click(headerCheckbox);
		waitResponse();
		
		click(listItemOne);
		waitResponse();
		
		Assertions.assertFalse(headerCheckbox.hasClass("z-listheader-checked"));
		
		click(listItemOne);
		waitResponse();
		
		Assertions.assertTrue(headerCheckbox.hasClass("z-listheader-checked"));
		
		click(headerCheckbox);
		waitResponse();
		
		Assertions.assertFalse(headerCheckbox.hasClass("z-listheader-checked"));
		Assertions.assertFalse(listItemOne.hasClass("z-listitem-selected"));
		Assertions.assertFalse(listItemTwo.hasClass("z-listitem-selected"));
	}
}
