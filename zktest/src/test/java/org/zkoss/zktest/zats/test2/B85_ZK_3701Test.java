/* B85_ZK_3701Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 10 12:25:48 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B85_ZK_3701Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		Assertions.assertEquals("zk-3701-1\nzk-3701-d1", getZKLog());
		closeZKLog();
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertEquals("zk-3701-2", getZKLog());
		closeZKLog();
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertEquals("zk-3701-d2", getZKLog());
	}
}
