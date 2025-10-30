/* B95_ZK_4713Test.java

	Purpose:

	Description:

	History:

		Tue Dec 08 16:19:32 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author katherinelin
 */
public class B95_ZK_4713Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		sleep(1000);
		Assertions.assertEquals("onPageSize", getZKLog());
	}
}