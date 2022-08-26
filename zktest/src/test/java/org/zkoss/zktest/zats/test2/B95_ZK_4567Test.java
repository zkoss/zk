/* B95_ZK_4567Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Dec 08 15:15:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_4567Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery orangeDiv = jq("$buttonDiv");
		JQuery treeHead = jq("@tree").find(".z-tree-header");
		JQuery frozenInner = jq(".z-frozen-inner").eq(0);
		int columnWidth = jq(".z-treecol").outerWidth();

		// first scroll test
		frozenInner.scrollLeft(3 * columnWidth + 30);
		waitResponse();
		int headScrollLeft = treeHead.scrollLeft();
		click(orangeDiv);
		waitResponse();
		Assertions.assertEquals(headScrollLeft, treeHead.scrollLeft(), "should not see the scroll position jumping.");
		Assertions.assertEquals("fz start: 3", getZKLog(), "the server side Frozen start value should sync as scroll position.");
		closeZKLog();

		// second scroll test
		frozenInner.scrollLeft(8 * columnWidth + 10);
		waitResponse();
		headScrollLeft = treeHead.scrollLeft();
		click(orangeDiv);
		waitResponse();
		Assertions.assertEquals(headScrollLeft, treeHead.scrollLeft(), "should not see the scroll position jumping.");
		Assertions.assertEquals( "fz start: 8", getZKLog(), "the server side Frozen start value should sync as scroll position.");
		closeZKLog();

		// setStart test
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertEquals(5 * columnWidth, treeHead.scrollLeft(), "the scroll(tree) position should be updated.");
		Assertions.assertEquals(5 * columnWidth, frozenInner.scrollLeft(), "the scroll(bar) position should be updated.");
		click(orangeDiv);
		waitResponse();
		Assertions.assertEquals("fz start: 5", getZKLog(), "the server side Frozen start value should sync as scroll position.");
	}
}
