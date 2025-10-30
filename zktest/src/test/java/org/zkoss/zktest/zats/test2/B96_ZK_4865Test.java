/* B96_ZK_4865Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 04 18:11:47 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4865Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertTrue(jq("$title").is(":focus"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertTrue(jq("$title").is(":focus"));
	}
}
