/* B95_ZK_4566Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 2 15:29:38 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4566Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		Assertions.assertEquals("false", getZKLog());
	}
}
