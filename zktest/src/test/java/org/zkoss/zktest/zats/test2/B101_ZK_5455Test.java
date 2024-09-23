/* B101_ZK_5455Test.java

	Purpose:

	Description:

	History:
		5:47 PM 2024/9/20, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5455Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		mouseOver(jq(".z-button"));
		waitResponse();
		assertNoZKError();
	}
}
