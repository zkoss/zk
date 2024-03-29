/* F85_ZK_3181Test.java

	Purpose:
		
	Description:
		
	History:
		Mon May 28 18:42:23 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F85_ZK_3181Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		waitResponse();
		String zkLog = getZKLog();
		click(jq("@button"));
		waitResponse();
		click(jq("@button"));
		waitResponse();

		Assertions.assertEquals(zkLog, getZKLog());
	}
}
