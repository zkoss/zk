/* B90_ZK_3942Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 29 15:07:09 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_3942Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertFalse(jq(".z-apply-loading").exists());
		Assertions.assertEquals("onCreate\nonClientInfo", getZKLog());
	}
}