/* B50_2918527Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 12:42:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2918527Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int left = jq("@textbox").offsetLeft();

		click(jq("@button"));
		waitResponse();

		int leftAfter = jq("@textbox").offsetLeft();

		assertThat("should move to center", leftAfter, greaterThan(left));
	}
}
