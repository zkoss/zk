/* B86_ZK_4091Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 25 15:42:24 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4091Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@groupbox:eq(0) .z-groupbox-title"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable(), "JavaScript exception");
		closeZKLog();

		click(jq("@groupbox:eq(1) @caption"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable(), "JavaScript exception");
	}
}
