/* B80_ZK_3010Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jan 19, 2016  3:45:28 PM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.zkoss.zktest.zats.WebDriverTestCase;

import junit.framework.Assert;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_3010Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		
		// click to focus on chosenbox
		click(jq(".z-chosenbox-input"));
		waitResponse(true);
		
		// type in "ban"
		type(jq(".z-chosenbox-input"), "ban");
		waitResponse(true);
		// check that there are two options
		Assert.assertTrue(jq(".z-chosenbox-option").length() == 2);
		// then select "banana"
		click(jq(".z-chosenbox-option").eq(1));
		waitResponse(true);
		// check that banana is selected
		Assert.assertEquals("banana", jq(".z-chosenbox-item-content").text());
		// type in "ban" again
		type(jq(".z-chosenbox-input"), "ban");
		waitResponse(true);
		// check that there are only one option
		Assert.assertTrue(jq(".z-chosenbox-option").length() == 1);
		// press enter to create and select the new tag
		if (jq("body").hasClass("gecko")) {
			sendKeys(jq(".z-chosenbox-input"), Keys.RETURN);
		} else {
			sendKeys(jq(".z-chosenbox-input"), Keys.ENTER);
		}
		// check that ban and banana are both selected
		Assert.assertEquals("banana", jq(".z-chosenbox-item-content").eq(0).text());
		Assert.assertEquals("ban", jq(".z-chosenbox-item-content").eq(1).text());
	}
}
