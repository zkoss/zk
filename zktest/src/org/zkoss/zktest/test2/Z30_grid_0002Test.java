/* Z30_grid_0002Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 4:29:35 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.junit.Test;
import org.zkoss.zktest.Element;
import org.zkoss.zktest.Widget;
import org.zkoss.zktest.ZKClientTestCase;

import com.thoughtworks.selenium.Selenium;

/**
 * @author jumperchen
 *
 */
public class Z30_grid_0002Test extends ZKClientTestCase {
	public Z30_grid_0002Test() {
		target = "Z30-grid-0002.zul";
	}
	@Test(expected = AssertionError.class)
	public void testClick() {
		String col1 = uuid(11);
		String col2 = uuid(12);
		String col3 = uuid(13);
		String col4 = uuid(14);
		
		Widget label1 = widget(33);
		String btn1 = uuid(36);
		Widget label2 = widget(41);
		String btn2 = uuid(44);
		Widget label3 = widget(49);
		String btn3 = uuid(52);
		Widget label4 = widget(57);
		String btn4 = uuid(60);
		Widget label5= widget(65);
		String btn5 = uuid(68);
		Widget label6 = widget(73);
		String btn6 = uuid(76);
		for (Selenium browser : browsers) {
			try {
				start(browser);

				click(col1);
				assertEquals("Click on column 1", label1.get("value"));
				click(btn1);
				
				contextMenu(col2);
				assertEquals("onRightClick on column 2", label2.get("value"));
				click(btn2);
				
				doubleClick(col3);
				assertEquals("onDoubleClick on column 3", label3.get("value"));
				click(btn3);

				click(col4);
				assertEquals("Click on column 4", label4.get("value"));
				click(btn4);

				contextMenu(col4);
				assertEquals("onRightClick on column 4", label5.get("value"));
				click(btn5);

				doubleClick(col4);
				assertEquals("onDoubleClick on column 4", label6.get("value"));
				click(btn6);
			} finally {
				stop();
			}
		}
	}
}
