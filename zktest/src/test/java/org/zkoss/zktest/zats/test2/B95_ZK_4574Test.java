/* B95_ZK_4574Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 13 10:22:33 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4574Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(jq("$div1").height(), jq("$div1 @label").height(), 1);
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		assertEquals(jq("$div1").height(), jq("$div2").height() * 2, 1);
		click(jq("@button").eq(2));
		waitResponse();
		assertEquals(jq("$div1").height(), jq("$div2").height(), 1);
		click(jq("@button").eq(3));
		waitResponse();
		assertEquals(jq("$div1").height(), jq("$div1 @label").height(), 1);
	}
}
