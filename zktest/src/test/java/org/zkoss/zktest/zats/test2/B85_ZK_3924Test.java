/* B85_ZK_3924Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 12 17:13:41 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3924Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		sleep(500);
		click(jq(".z-icon-times"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
		Assertions.assertFalse(jq(".z-notification").exists());
	}
}
