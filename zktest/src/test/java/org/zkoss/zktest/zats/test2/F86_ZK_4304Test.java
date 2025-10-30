/* F86_ZK_4304Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 28 12:17:45 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F86_ZK_4304Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertTrue(jq(".z-combobox-icon").hasClass("z-icon-caret-down"));
		Assertions.assertTrue(jq(".z-bandbox-icon").hasClass("z-icon-search"));
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertTrue(jq(".z-combobox-icon").hasClass("z-icon-calendar"));
		Assertions.assertTrue(jq(".z-bandbox-icon").hasClass("z-icon-calendar"));
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertTrue(jq(".z-combobox-icon").hasClass("z-icon-search"));
		Assertions.assertTrue(jq(".z-bandbox-icon").hasClass("z-icon-search"));
	}
}
