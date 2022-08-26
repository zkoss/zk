/* B96_ZK_5121Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 28 16:37:12 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5121Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		click(jq(".z-searchbox-icon.z-icon-caret-down"));
		waitResponse(4000);
		assertFalse(jq(".z-searchbox-popup").is(":visible"));

		click(jq("@button"));
		click(jq(".z-cascader-icon.z-icon-caret-down"));
		waitResponse(4000);
		assertFalse(jq(".z-cascader-popup").is(":visible"));
	}
}