/* B96_ZK_4970Test.java

	Purpose:
		
	Description:
		
	History:
		4:37 PM 2021/11/18, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_4970Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(create)"));
		waitResponse();
		assertEquals(3, jq(".z-listitem").length());
	}
}
