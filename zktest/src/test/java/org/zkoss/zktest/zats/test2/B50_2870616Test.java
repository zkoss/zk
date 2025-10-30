/* B50_2870616Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 14:58:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2870616Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		rightClick(jq("@label"));
		sleep(1000); // can't use waitResponse since the loading mask might not go away if this bug exists

		Assertions.assertTrue(jq("@menupopup").isVisible());
		Assertions.assertFalse(jq(".z-apply-mask").exists());
	}
}
