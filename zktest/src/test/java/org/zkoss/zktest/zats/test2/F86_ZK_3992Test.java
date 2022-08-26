/* F86_ZK_3992Test.java

	Purpose:

	Description:

	History:
		Mon Sep 17 17:23:52 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author wenninghsu
 */
public class F86_ZK_3992Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq(".z-button").eq(0));
		waitResponse();
		assertTrue("testing".equals(getZKLog()));
		click(jq(".z-button").eq(1));
		waitResponse();
		assertTrue("testi".equals(getZKLog().split("\n")[1]));
	}

}
