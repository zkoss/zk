/* B86_ZK_4119Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Nov 06 10:01:34 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4119Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		testAlignment(jq(".z-columns > .z-column"), jq(".z-row > .z-cell"));
		testAlignment(jq(".z-listhead > .z-listheader"), jq(".z-listitem > .z-listcell"));
		testAlignment(jq(".z-treecols > .z-treecol"), jq(".z-treerow > .z-treecell"));

	}

	private void testAlignment(JQuery headers, JQuery cells) {
		for (int i = 0; i < headers.length(); i++) {
			Assertions.assertEquals(headers.eq(i).offsetLeft(), cells.eq(i).offsetLeft());
		}
	}
}
