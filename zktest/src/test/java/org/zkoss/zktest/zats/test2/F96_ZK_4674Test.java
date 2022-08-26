/* F96_ZK_4674Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 02 17:47:40 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F96_ZK_4674Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		try {
			click(jq(".z-a:contains(Atlantic)"));
			waitResponse();
			click(jq("@button"));
			waitResponse();
			Assertions.assertTrue(
					getZKLog().contains("https://fonts.googleapis.com/css?family=Open+Sans"),
					"google font should be added with HTTPS");
		} finally {
			click(jq(".z-a:contains(Default)"));
			waitResponse();
		}
	}
}
