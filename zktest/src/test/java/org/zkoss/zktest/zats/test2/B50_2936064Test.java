/* B50_2936064Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 15:10:39 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2936064Test extends WebDriverTestCase {
	@Test
	public void test1() {
		connect();

		click(jq("@button:contains(group1)"));
		waitResponse();
		Assertions.assertFalse(jq("@group:contains(group1)").exists());
		Assertions.assertFalse(jq("@groupfoot:contains(groupfoot1)").exists());
	}

	@Test
	public void test2() {
		connect();

		int gridHeight = jq("@grid").outerHeight();

		click(jq("@button:contains(groupfoot1)"));
		waitResponse();
		Assertions.assertTrue(jq("@group:contains(group1)").exists());
		Assertions.assertFalse(jq("@groupfoot:contains(groupfoot1)").exists());

		click(jq("@button:contains(group1)"));
		waitResponse();
		Assertions.assertFalse(jq("@group:contains(group1)").exists());
		assertThat("grid height should shrink", jq("@grid").outerHeight(), lessThan(gridHeight));
	}
}
