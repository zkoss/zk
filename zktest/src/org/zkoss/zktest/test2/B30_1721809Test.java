/* B30_1721809Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 16, 2009 5:52:16 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.junit.Test;
import org.zkoss.zktest.ZKClientTestCase;

import com.thoughtworks.selenium.Selenium;

/**
 * @author jumperchen
 *
 */
public class B30_1721809Test extends ZKClientTestCase {
	
	public B30_1721809Test() {
		target = "B30-1721809.zul";
	}
	
	@Test(expected = AssertionError.class)
	public void test1() {

		String comp1 = uuid(4);
		String comp2 = uuid(6);
		
		for (Selenium browser : browsers) {
			try {
				start(browser);
				
				click(comp1);
				assertEquals(getText(comp2), "click");
				
				click(comp1);
				assertEquals(getText(comp2), "click click");
				
			} finally {
				stop();
			}
		}
	}
}
