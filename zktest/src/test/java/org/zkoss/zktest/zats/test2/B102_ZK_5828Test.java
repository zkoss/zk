/* B102_ZK_5828Test.java

	Purpose:

	Description:

	History:
		Wed May 14 11:51:14 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B102_ZK_5828Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery jqCombobox = jq("@combobox");
		selectComboitem(jqCombobox.toWidget(), 1);
		waitResponse();
		Assertions.assertEquals(1, getZKLog().split("\n").length);
		type(jqCombobox.find("input"), "");
		waitResponse();
		Assertions.assertEquals(2, getZKLog().split("\n").length);
	}
}