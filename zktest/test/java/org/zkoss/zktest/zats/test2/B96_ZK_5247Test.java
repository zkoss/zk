/* B96_ZK_5247Test.java

	Purpose:
		
	Description:
		
	History:
		3:29 PM 2022/9/30, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5247Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int width = jq(".z-row-inner").width();
		assertTrue(width > 60);

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		click(jq("@button:eq(0)"));
		waitResponse();
		assertEquals(width, jq(".z-row-inner").width(), 5);
	}
}
