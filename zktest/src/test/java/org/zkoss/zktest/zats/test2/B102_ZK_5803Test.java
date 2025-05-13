/* B102_ZK_5803Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 13 10:44:24 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */

public class B102_ZK_5803Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		assertEquals(0, jq("#z_ddstkup").length());
	}
}
