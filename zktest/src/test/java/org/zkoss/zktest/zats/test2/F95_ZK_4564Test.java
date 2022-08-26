/* F95_ZK_4564Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 7 11:32:33 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F95_ZK_4564Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();
		sleep(3000);
		Assertions.assertEquals("loaded!", getZKLog());
		closeZKLog();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("Clicked!", getZKLog());
	}
}
