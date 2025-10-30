/* B50_ZK_714Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 17:54:21 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_714Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:contains(Reload)"));
		waitResponse();
		for (int i = 0; i < 10; i++) {
			Assertions.assertEquals("Item" + i, jq("@listcell").eq(i).text().trim());
		}
	}
}
