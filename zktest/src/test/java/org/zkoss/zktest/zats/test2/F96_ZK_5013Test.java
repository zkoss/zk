/* F96_ZK_5013Test.java

	Purpose:
		
	Description:
		
	History:
		6:05 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class F96_ZK_5013Test  extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-treecol-checkable"));
		waitResponse();
		assertEquals("onCheckSelectAll", getZKLog());
	}
}
