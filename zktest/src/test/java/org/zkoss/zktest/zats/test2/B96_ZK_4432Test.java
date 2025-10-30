/* B96_ZK_4432Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 16 18:26:51 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4432Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse(); // Fail if an unexpected alert was opened
	}
}
