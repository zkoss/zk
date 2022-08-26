/* B90_ZK_4402Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 4 19:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4402Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertTrue(jq("@step:eq(1)").exists());
	}
}
