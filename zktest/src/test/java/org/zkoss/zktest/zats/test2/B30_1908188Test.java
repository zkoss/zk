/* B30_1908188Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 14 17:01:47 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B30_1908188Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery menu = jq("$testMenu");
		int menuX = menu.positionLeft();
		int menuY = menu.positionTop();

		click(menu);
		waitResponse();
		click(jq("@menuitem:first"));
		waitResponse();
		MatcherAssert.assertThat(
				"The popup must be at right and below of the menu",
				jq(".z-popup").positionLeft(), greaterThan(menuX));
		MatcherAssert.assertThat(
				"The popup must be at right and below of the menu",
				jq(".z-popup").positionTop(), greaterThan(menuY));

		click(menu);
		waitResponse();
		click(jq("@menuitem:eq(1)"));
		waitResponse();
		Assertions.assertEquals(50, jq(".z-popup").positionLeft(), "The popup must be at position (50,50)");
		Assertions.assertEquals(50, jq(".z-popup").positionTop(), "The popup must be at position (50,50)");
	}
}
