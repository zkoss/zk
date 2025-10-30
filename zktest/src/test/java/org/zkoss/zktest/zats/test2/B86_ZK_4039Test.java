/* B86_ZK_4039Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 27 12:37:42 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4039Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertFalse(isZKLogAvailable(), "Unexpected zklog");
	}
}
