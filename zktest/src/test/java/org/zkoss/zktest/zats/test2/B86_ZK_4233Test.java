/* B86_ZK_4233Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 14 10:34:21 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4233Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button:contains(show and refresh)"));
		waitResponse();
		Assert.assertEquals(1, jq(".z-messagebox-window").length());
		Assert.assertEquals("B86-ZK-4233-include.zul?test=0", getZKLog());
		
		click(jq(".z-window-close"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-messagebox-window").length());
		
		click(jq("@button:contains(show and refresh)"));
		waitResponse();
		Assert.assertEquals(1, jq(".z-messagebox-window").length());
		Assert.assertEquals("B86-ZK-4233-include.zul?test=0\nB86-ZK-4233-include.zul?test=1", getZKLog());
	}
}
