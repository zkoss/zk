/* B30_1553937Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/9/16 �U��5:01:13 , Created by sam
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
public class B30_1553937Test extends ZKClientTestCase {

	public B30_1553937Test() {
		target = "B30-1553937.zul";
	}

	@Test(expected = AssertionError.class)
	public void test1() {

		String comp1 = uuid(4);
		String comp2 = uuid(7);
		for (Selenium browser : browsers) {
			try {
				start(browser);

				int comp1Top = getElementPositionTop(comp1).intValue();
				int comp2Top = getElementPositionTop(comp2).intValue();
				// comp2 should beside comp1
				assertTrue(Math.abs(comp1Top - comp2Top) < 10);

			} finally {
				stop();
			}
		}
	}
}
