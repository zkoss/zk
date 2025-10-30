/* B96_ZK_4387Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 07 11:42:50 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4387Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(append)"));
		waitResponse();
		click(jq("@chosenbox"));
		Assertions.assertEquals("Moon", jq(".z-chosenbox-option:last").text(), "Append failed");

		click(jq("@button:contains(prepend)"));
		waitResponse();
		click(jq("@chosenbox"));
		Assertions.assertEquals("Mars", jq(".z-chosenbox-option:first").text(), "Prepend failed");
	}
}
