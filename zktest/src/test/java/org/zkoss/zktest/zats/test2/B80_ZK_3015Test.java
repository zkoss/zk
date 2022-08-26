/* F80_ZK_2584Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 18 14:48:53 CST 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;



/**
 * @author jameschu
 */
public class B80_ZK_3015Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery button = jq("button");
		click(button.get(0));
		waitResponse();
		assertEquals("Value1", jq(".z-combobox-input").val());
	}
}
