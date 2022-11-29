/* B80_ZK_3066Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 11 14:36:03 CST 2016, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3066Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery dpLabel = jq("$dp");
		assertEquals("ccc", dpLabel.text());
	}
}
