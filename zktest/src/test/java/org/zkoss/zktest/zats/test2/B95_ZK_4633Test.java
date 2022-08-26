/* B95_ZK_4633Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Jul 30, 2020 02:48:18 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4633Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		Assertions.assertTrue(jq("@rangeslider").toWidget().is("disabled"));
		Assertions.assertTrue(jq("@multislider").toWidget().is("disabled"));
	}
}
