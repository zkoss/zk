/* B85_ZK_3905Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 30 12:25:29 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B85_ZK_3905Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		JQuery headerCheckbox = jq(".z-listheader-checkable");
		JQuery listItemOne = jq(".z-listitem").eq(0);
		JQuery listItemTwo = jq(".z-listitem").eq(1);
		
		click(headerCheckbox);
		waitResponse();
		
		Assert.assertTrue(headerCheckbox.hasClass("z-listheader-checked"));
		Assert.assertTrue(listItemOne.hasClass("z-listitem-selected"));
		Assert.assertTrue(listItemTwo.hasClass("z-listitem-selected"));
		
		click(jq(".z-button"));
		waitResponse();
		
		Assert.assertFalse(headerCheckbox.hasClass("z-listheader-checked"));
		Assert.assertFalse(listItemOne.hasClass("z-listitem-selected"));
		Assert.assertFalse(listItemTwo.hasClass("z-listitem-selected"));
		
		click(headerCheckbox);
		waitResponse();
		
		click(listItemOne);
		waitResponse();
		
		Assert.assertFalse(headerCheckbox.hasClass("z-listheader-checked"));
		
		click(listItemOne);
		waitResponse();
		
		Assert.assertTrue(headerCheckbox.hasClass("z-listheader-checked"));
		
		click(headerCheckbox);
		waitResponse();
		
		Assert.assertFalse(headerCheckbox.hasClass("z-listheader-checked"));
		Assert.assertFalse(listItemOne.hasClass("z-listitem-selected"));
		Assert.assertFalse(listItemTwo.hasClass("z-listitem-selected"));
	}
}
