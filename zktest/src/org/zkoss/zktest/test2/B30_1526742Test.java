/* B30_1526742.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/9/16 �U��3:40:42 , Created by sam
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
public class B30_1526742Test extends ZKClientTestCase {
	public B30_1526742Test() {
		target = "B30-1526742.zul";
	}

	@Test(expected = AssertionError.class)
	public void test1() {
		String compParent = uuid(2);
		String compId1 = uuid(3);
		String compId2 = uuid(5);

		for (Selenium browser : browsers) {
			try {
				start(browser);

				int totalWidth = getElementWidth(compParent).intValue();
				int comp1Right = getElementPositionLeft(compId1).intValue()
						+ getElementWidth(compId1).intValue();
				int comp2Left = getElementPositionLeft(compId2).intValue();
				int diff = comp2Left - comp1Right;

				// comp1 and comp2 has about 20% space
				assertTrue((totalWidth * 0.2 - diff) < 50);

			} finally {
				stop();
			}
		}
	}
}
