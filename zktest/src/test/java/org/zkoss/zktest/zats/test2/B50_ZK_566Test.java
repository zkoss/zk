/* B50_ZK_566Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 19 17:43:50 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_566Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertTrue(jq("@treecell").eq(0).text().trim().contains("item 2"));
		Assertions.assertTrue(jq("@treecell").eq(1).text().trim().contains("item 1"));
		Assertions.assertTrue(jq("@treecell").eq(2).text().trim().contains("item 0"));
	}
}
