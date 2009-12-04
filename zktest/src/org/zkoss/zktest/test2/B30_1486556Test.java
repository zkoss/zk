/* B30_1486556Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkoss.zktest.test2;

import org.junit.Test;
import org.zkoss.zktest.Jquery;
import org.zkoss.zktest.ZKClientTestCase;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author sam
 * 
 */
public class B30_1486556Test extends ZKClientTestCase {

	public B30_1486556Test() {
		target = "B30-1486556.zul";
	}

	@Test(expected = AssertionError.class)
	public void test1() {
		String inputComp = uuid(4);
		String buttonComp = uuid(5);
		Jquery jq = jq(inputComp);
		
		for (Selenium browser : browsers) {
			try {
				start(browser);

				windowFocus();
				focus(inputComp);
				fireEvent(inputComp, "blur");
				assertTrue(jq.hasClass("z-textbox-text-invalid"));

				// Test case 2
				refresh();
				windowFocus();
				focus(inputComp);
				click(buttonComp);
				assertTrue(jq.hasClass("z-textbox-text-invalid"));
				
			} finally {
				stop();
			}
		}
	}
}
