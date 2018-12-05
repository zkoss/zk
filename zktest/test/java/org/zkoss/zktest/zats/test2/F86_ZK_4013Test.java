/* F86_ZK_4013Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 05 12:13:30 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_4013Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		testBy(jq(".z-listhead"), jq(".z-listheader"));
		testBy(jq(".z-columns"), jq(".z-column"));
		testBy(jq(".z-treecols"), jq(".z-treecol"));
	}

	private void testBy(JQuery head, JQuery header) {
		int firstHeaderWidth = header.eq(0).outerWidth();
		Assert.assertEquals(100, firstHeaderWidth, 2);

		int totalWidth = firstHeaderWidth;
		for (int i = 1; i < header.length(); i++) {
			int headerWidth = header.eq(i).outerWidth();
			if (i > 1)
				Assert.assertEquals(header.eq(i - 1).outerWidth(), headerWidth, 2);
			totalWidth += headerWidth;
		}
		Assert.assertEquals(head.outerWidth(), totalWidth, 2);
	}
}