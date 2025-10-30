/* B96_ZK_5044Test.java

	Purpose:
		
	Description:
		
	History:
		3:59 PM 2021/11/8, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5044Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertTrue(80 < jq(".z-combobox-input").outerWidth());
	}
}