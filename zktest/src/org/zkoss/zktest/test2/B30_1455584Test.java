/* B30_1455584Test.java

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
import org.zkoss.zktest.ZKClientTestCase;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author sam
 * 
 */
public class B30_1455584Test extends ZKClientTestCase {

	public B30_1455584Test() {
		target = "B30-1455584.zul";
	}

	@Test(expected = AssertionError.class)
	public void test1() {
		String testComp = uuid(4);
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				String strClickBefor = getText(testComp);
				focus(testComp);
				click(testComp);

				assertNotEquals(strClickBefor, getText(testComp));				
			} finally {
				stop();
			}
		}
	}

}
