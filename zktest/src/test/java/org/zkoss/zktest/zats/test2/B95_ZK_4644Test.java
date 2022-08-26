/* B95_ZK_4644Test.java

	Purpose:

	Description:

	History:
		Wed Oct 28 11:10:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4644Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		sleep(1000);
		click(jq("@button"));
		sleep(1000);
		assertFalse(hasError());
	}
}