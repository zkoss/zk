/* B50_2981016Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 11:42:50 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2981016Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals(80, jq("@combobox").outerWidth());
	}
}
