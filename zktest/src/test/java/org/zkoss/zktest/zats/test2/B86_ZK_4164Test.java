/* B86_ZK_4164Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 11:54:25 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4164Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		int scrollHeight = jq("@grid > .z-grid-body").scrollHeight();
		jq("@grid > .z-grid-body").scrollTop(scrollHeight / 2);
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
