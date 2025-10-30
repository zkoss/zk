/* B86_ZK_4141Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Dec 24 17:49:34 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4141Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$test"));
		waitResponse();
		click(jq("$log"));
		waitResponse();
		Assertions.assertEquals("#/error/404", getZKLog());
	}
}
