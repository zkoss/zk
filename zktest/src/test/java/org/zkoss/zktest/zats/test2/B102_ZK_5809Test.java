/* B102_ZK_5809Test.java

	Purpose:

	Description:

	History:
		Tue Mar 25 10:22:40 CST 2025, Created by jameschu

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
public class B102_ZK_5809Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqRadio = jq("@radio");
		assertEquals(true, jqRadio.eq(0).toWidget().is("disabled"));
		assertEquals(true, jqRadio.eq(1).toWidget().is("disabled"));
	}
}
