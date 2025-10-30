/* B50_ZK_294Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 16:27:23 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_294Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertEquals("This shall not have <span>", jq("span").eq(1).text().trim());
		Assertions.assertEquals("<span> inside textarea", jq("textarea").eq(0).text().trim());
	}
}
