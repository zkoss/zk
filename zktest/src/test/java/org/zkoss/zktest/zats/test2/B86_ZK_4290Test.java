/* B86_ZK_4290Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 24 10:10:10 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4290Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertEquals("Thu Apr 01 2038 00:00:00 GMT-0600\nThu Apr 01 2038 00:00:00 GMT+0200", getZKLog());
	}
}
