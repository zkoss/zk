/* B96_ZK_4496Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 05 16:56:27 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4496Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("button"));
		waitResponse();

		Assertions.assertNotEquals("Template not found!", getZKLog());
	}
}
