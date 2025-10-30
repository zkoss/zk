/* B95_ZK_4694Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 07 15:14:15 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4694Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse(); // Test fails if processing hangs
		Assertions.assertEquals("onClientInfonull", getZKLog());
	}
}
