/* B30_1568393Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 17, 2009 10:36:54 AM , Created by sam
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
public class B30_1568393Test extends ZKClientTestCase {

	public B30_1568393Test() {
		target = "B30-1568393.zul";
	}

	@Test(expected = AssertionError.class)
	public void test1() {
		String comp1 = uuid(5);
		for (Selenium browser : browsers) {
			try {
				start(browser);

				int x = getElementPositionLeft(comp1).intValue();
				int y = getElementPositionTop(comp1).intValue();

				for (int i = 0; i < 6; i++) {
					mouseDown(comp1);
					mouseUp(comp1);
				}

				int x2 = getElementPositionLeft(comp1).intValue();
				int y2 = getElementPositionTop(comp1).intValue();

				// after several mouse click, the comp should not move it
				// position
				assertEquals(x, x2);
				assertEquals(y, y2);

			} finally {
				stop();
			}
		}
	}

}
