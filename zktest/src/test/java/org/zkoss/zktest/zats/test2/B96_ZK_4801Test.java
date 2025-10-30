/* B96_ZK_4801Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 25 15:53:21 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_4801Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery button = jq("@button");
		click(button);
		waitResponse();
		Assertions.assertFalse(
				getZKLog().contains("fonts.googleapis.com/css?family=Open+Sans"),
				"google font shouldn't be added if atlantic theme is not active");
		closeZKLog();


		try {
			click(jq(".z-a:contains(Atlantic)"));
			waitResponse();
			click(button);
			waitResponse();
			Assertions.assertTrue(
					getZKLog().contains("fonts.googleapis.com/css?family=Open+Sans"),
					"google font should be added if atlantic theme is active");
		} finally {
			click(jq(".z-a:contains(Default)"));
			waitResponse();
		}
	}
}

