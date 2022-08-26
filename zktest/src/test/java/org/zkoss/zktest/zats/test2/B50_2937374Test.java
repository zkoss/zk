/* B50_2937374Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 15:33:17 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2937374Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse(true);
		Assertions.assertFalse(jq(".z-combobox-popup").exists());
	}
}
