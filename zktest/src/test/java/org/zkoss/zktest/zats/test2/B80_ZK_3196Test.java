/** B80_ZK_3196Test.java.

	Purpose:
		
	Description:
		
	History:
		Tue May 03 16:10:02 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3196Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		selectComboitem(jq("@combobox").toWidget(), 0);
		waitResponse();
		click(jq("$show"));
		waitResponse();
		JQuery proxyVal = jq("$p_val");
		JQuery originVal = jq("$o_val");
		assertEquals("B80_ZK_3196Object [name=Marie]", proxyVal.text());
		assertEquals("B80_ZK_3196Object [name=Paul]", originVal.text());
		click(jq("$save"));
		waitResponse();
		assertEquals("B80_ZK_3196Object [name=Marie]", proxyVal.text());
		assertEquals("B80_ZK_3196Object [name=Marie]", originVal.text());
	}
}
