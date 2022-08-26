/* B85_ZK_3830Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 15 12:42:38 CST 2018, Created by klyvechen

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author klyvechen
 */
public class B85_ZK_3830Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		selectComboitem(jq(".z-combobox").toWidget(), 0);
		waitResponse();
		selectComboitem(jq(".z-combobox").toWidget(), 2);
		waitResponse();
		assertFalse(jq("＠button").exists());
	}
}

