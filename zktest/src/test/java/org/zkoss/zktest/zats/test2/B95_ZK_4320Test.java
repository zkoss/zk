/* B95_ZK_4320Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Oct 07 16:58:43 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4320Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		jq(".z-frozen-inner").scrollLeft(400);
		waitResponse();
		int c10Left = jq("$c10").offsetLeft();
		int c10Width = jq("$c10").outerWidth();
		click(jq("$c10"));
		waitResponse();
		Assert.assertEquals("column shouldn't move after sorting", c10Left, jq("$c10").offsetLeft());
		Assert.assertEquals("column width shouldn't change after sorting", c10Width, jq("$c10").outerWidth());
	}
}
