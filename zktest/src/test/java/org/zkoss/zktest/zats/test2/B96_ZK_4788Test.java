/* B96_ZK_4788Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 08 12:05:14 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

public class B96_ZK_4788Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:contains(setCols(0))"));
		click(jq("@button:contains(hasSizeAttribute)"));
		waitResponse();
		assertEquals("false\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse", getZKLog());
	}
}
