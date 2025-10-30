/* B85_ZK_3908Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 12:05:47 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3908Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-toolbarbutton").eq(0).offsetLeft() == jq(".z-toolbarbutton").eq(3).offsetLeft(),
				"not vertical");
		
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertTrue(jq(".z-toolbarbutton").eq(0).offsetTop() == jq(".z-toolbarbutton").eq(3).offsetTop(),
				"not horizontal");
		
		click(jq("@button").eq(2));
		waitResponse();
		Assertions.assertTrue(jq(".z-toolbarbutton").eq(0).offsetLeft() == jq(".z-toolbarbutton").eq(3).offsetLeft(),
				"not vertical");
	}
}
