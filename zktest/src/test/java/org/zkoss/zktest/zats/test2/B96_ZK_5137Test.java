/* B96_ZK_5137Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 28 10:00:36 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_5137Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery flexItemDiv = jq("$flexItem");
		click(jq("$setBtn"));
		waitResponse();
		assertTrue(flexItemDiv.hasClass("z-flex-item"));
		click(jq("$toggleBtn"));
		waitResponse();
		assertTrue(flexItemDiv.hasClass("z-flex-item"));
		click(jq("$addBtn"));
		waitResponse();
		assertTrue(flexItemDiv.hasClass("z-flex-item"));
	}
}
