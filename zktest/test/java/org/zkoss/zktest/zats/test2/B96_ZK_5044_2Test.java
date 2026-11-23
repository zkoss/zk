/* B96_ZK_5044_2Test.java

	Purpose:
		
	Description:
		
	History:
		12:20 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5044_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertTrue(100 < jq(".z-combobox-input").outerWidth());
		assertTrue(300 > jq(".z-combobox-input").outerWidth());
	}
}