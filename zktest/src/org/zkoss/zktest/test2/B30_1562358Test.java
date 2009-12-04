/* B30_1562358Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 17, 2009 9:58:02 AM , Created by sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zktest.test2;

import org.junit.Test;
import org.zkoss.zktest.ZKClientTestCase;

import com.thoughtworks.selenium.Selenium;

/**
 * @author sam
 * 
 */
public class B30_1562358Test extends ZKClientTestCase {

	public B30_1562358Test() {
		target = "B30-1562358.zul";
	}

	@Test(expected = AssertionError.class)
	public void test1() {
		String cmp1 = uuid(6);

		for (Selenium browser : browsers) {
			try {
				start(browser);

				// comp should have 1 line
				assertTrue(getElementHeight(cmp1).intValue() < 35);

			} finally {
				stop();
			}
		}
	}
}
