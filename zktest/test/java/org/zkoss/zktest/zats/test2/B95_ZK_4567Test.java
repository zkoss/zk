/* B95_ZK_4567Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Dec 08 15:15:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertEquals("should not see the scroll position jumping.", headScrollLeft, treeHead.scrollLeft());
		Assert.assertEquals("the server side Frozen start value should sync as scroll position.", "fz start: 3", getZKLog());
		closeZKLog();

		// second scroll test
		frozenInner.scrollLeft(8 * columnWidth + 10);
		waitResponse();
		headScrollLeft = treeHead.scrollLeft();
		click(orangeDiv);
		waitResponse();
		Assert.assertEquals("should not see the scroll position jumping.", headScrollLeft, treeHead.scrollLeft());
		Assert.assertEquals("the server side Frozen start value should sync as scroll position.", "fz start: 8", getZKLog());
		closeZKLog();

		// setStart test
		click(jq("@button").eq(0));
		waitResponse();
		Assert.assertEquals("the scroll(tree) position should be updated.", 5 * columnWidth, treeHead.scrollLeft());
		Assert.assertEquals("the scroll(bar) position should be updated.", 5 * columnWidth, frozenInner.scrollLeft());
		click(orangeDiv);
		waitResponse();
		Assert.assertEquals("the server side Frozen start value should sync as scroll position.", "fz start: 5", getZKLog());
	}
}
