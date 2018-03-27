/* B85_ZK_3313Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Mar 26 10:01:32 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.*;

public class B85_ZK_3313Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		assertEquals(jq(".z-listbox").eq(0).find("td > div").text(), "");
		assertEquals(jq(".z-grid").eq(0).find("td > div").text(), "");
		assertEquals(jq(".z-tree").eq(0).find("tbody").text(), "");

		assertEquals(jq(".z-listbox").eq(1).find("td > div").text(), "no items here");
		assertEquals(jq(".z-grid").eq(1).find("td > div").text(), "no items here");

		assertEquals(jq(".z-listbox").eq(2).find("td > div > span").text(), "squeeze");
		assertEquals(jq(".z-grid").eq(2).find("td > div > span").text(), "squeeze");
		assertEquals(jq(".z-tree").eq(1).find("td > div > span").eq(1).text(), "squeeze");
	}

}
