/* B96_ZK_4979Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 27 12:53:14 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4979Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		rightClick(jq("@button"));
		waitResponse();

		Assertions.assertFalse(isZKLogAvailable(),
				"Shouldn't trigger onRightClick");
	}
}
