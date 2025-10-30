/* B96_ZK_4833Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 29 15:47:05 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4833Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@detail"));
		waitResponse();
		click(jq(".cke_button.cke_button__source"));
		waitResponse();

		assertNoJSError();
	}
}
