/* B102_ZK_5681Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 08 20:29:45 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5681_2_Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/test2/B102-ZK-5681-2.zul");

		// Expand Groupbox
		click(jq(".z-groupbox-title"));
		waitResponse();
		sleep(1000);
		String label1 = jq(".z-checkbox-content").eq(0).text();
		assertEquals("Groupbox defer child", label1);
		// Expand Panel
		click(jq("@panel .z-icon-angle-down"));
		waitResponse();
		sleep(1000);
		String label2 = jq(".z-checkbox-content").eq(1).text();
		assertEquals("Panel defer child", label2);
		// Switch to Tab 2
		click(jq("@tab").eq(1));
		waitResponse();
		sleep(1000);
		String label3 = jq(".z-checkbox-content").eq(3).text();
		assertEquals("Tab 2 defer child", label3);
		// Open Combobox
		click(jq("@combobox .z-combobox-icon"));
		waitResponse();
		sleep(1000);
		String label4 = jq(".z-comboitem-text").eq(0).text();
		assertEquals("Item 1", label4);
		//Open Bandbox
		eval("jq('.z-bandbox-button')[0].click()");
		waitResponse();
		sleep(1200);
		String label5 = jq(".z-checkbox-content").eq(4).text();
		System.out.println(label5);
		assertEquals("Bandbox defer child", label5);
		// Open Menu and click slow item
		click(jq("@menu .z-menu-content"));
		waitResponse();
		String label6 = jq(".z-menuitem-text").eq(0).text();
		assertEquals("Slow item", label6);
	}
}


