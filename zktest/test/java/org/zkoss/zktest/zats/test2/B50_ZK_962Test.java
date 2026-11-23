/* B50_ZK_962Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 24 15:03:27 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_962Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals("Panel 1Panel 2Panel 3Panel 5", jq(".z-portalchildren-content").eq(0).children().text());
		Assert.assertEquals("Panel 4", jq(".z-portalchildren-content").eq(1).children().text());
		click(jq("@button:contains(single column)"));
		waitResponse();
		dragAndDrop(jq(".z-panel-head:contains(Panel 1)"), jq(".z-panel-head:contains(Panel 2)"));
		waitResponse();
		Assert.assertEquals("Panel 2Panel 1Panel 3Panel 5", jq(".z-portalchildren-content").eq(0).children().text());
		click(jq("@button:contains(two columns)"));
		waitResponse();
		Assert.assertEquals("Panel 2Panel 1Panel 3Panel 5", jq(".z-portalchildren-content").eq(0).children().text());
		Assert.assertEquals("Panel 4", jq(".z-portalchildren-content").eq(1).children().text());
	}
}
