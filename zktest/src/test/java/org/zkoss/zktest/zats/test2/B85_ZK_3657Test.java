/* B85_ZK_3657Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 21 18:31:08 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3657Test extends WebDriverTestCase {
	@Test
	public void testFlexMinWidth() {
		connect();

		String baseWidth = jq("$outer1").css("width");
		Assertions.assertEquals(baseWidth, jq("$outer2").css("width"),
				"The width of #outer2 is wrong!");
		Assertions.assertEquals(baseWidth, jq("$outer3").css("width"),
				"The width of #outer3 is wrong!");
	}

	@Test
	public void testFlexMinHeight() {
		connect();

		String baseHeight = jq("$outer1").css("height");
		Assertions.assertEquals(baseHeight, jq("$outer2").css("height"), "The height of #outer2 is wrong!");
		Assertions.assertEquals(baseHeight, jq("$outer3").css("height"), "The height of #outer3 is wrong!");
	}
}
