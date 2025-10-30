/* B86_ZK_3987Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 03 10:39:33 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_3987Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		String color1 = jq("@row:eq(0) @label:last").css("color");
		String color2 = jq("@row:eq(1) @label:last").css("color");
		Assertions.assertEquals(color1, color2, "The color of two labels are different.");
	}
}
