/* B90_ZK_4419Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 28 14:53:06 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B90_ZK_4419Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery div = jq("@div");
		Assertions.assertEquals(div.eq(0).outerWidth(), div.eq(1).outerWidth());
	}
}
