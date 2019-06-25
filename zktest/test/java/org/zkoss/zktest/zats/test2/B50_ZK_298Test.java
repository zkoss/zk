/* B50_ZK_298Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 08 10:42:00 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_298Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int lastHeight = jq(".z-tabbox").height();
		Assert.assertEquals(jq(".z-tabpanels").outerHeight(), jq(".z-tabbox").height());
		
		click(jq(".z-tab").eq(1));
		waitResponse();
		Assert.assertEquals(jq(".z-tabpanels").outerHeight(), jq(".z-tabbox").height());
		Assert.assertNotEquals(lastHeight, jq(".z-tabbox").height());
		lastHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(2));
		waitResponse();
		Assert.assertEquals(jq(".z-tabpanels").outerHeight(), jq(".z-tabbox").height());
		Assert.assertNotEquals(lastHeight, jq(".z-tabbox").height());
		
		click(jq(".z-button").eq(0));
		waitResponse();
		int fixedHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(1));
		waitResponse();
		Assert.assertEquals(fixedHeight, jq(".z-tabbox").height());
		
		click(jq(".z-tab").eq(0));
		waitResponse();
		Assert.assertEquals(fixedHeight, jq(".z-tabbox").height());
		
		click(jq(".z-button").eq(1));
		waitResponse();
		lastHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(1));
		waitResponse();
		Assert.assertNotEquals(lastHeight, jq(".z-tabbox").height());
		lastHeight = jq(".z-tabbox").height();
		
		click(jq(".z-tab").eq(2));
		waitResponse();
		Assert.assertNotEquals(fixedHeight, jq(".z-tabbox").height());
	}
}
