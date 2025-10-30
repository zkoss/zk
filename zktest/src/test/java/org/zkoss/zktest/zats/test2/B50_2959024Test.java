/* B50_2959024Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 16:32:51 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2959024Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		Assertions.assertTrue(jq(".z-messagebox-window").isVisible());
		assertThat(jq(".z-messagebox").text(), containsString("If you can see the message, the bug is fixed!"));
	}
}
