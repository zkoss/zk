/* B50_2944364Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 17:00:10 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2944364Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertFalse(jq("@ckeditor").isVisible());

		click(jq("@button"));
		waitResponse();
		Assertions.assertTrue(jq("@ckeditor").isVisible());
	}
}
