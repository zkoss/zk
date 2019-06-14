/* B85_ZK_3950Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 12 16:39:09 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3950Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(jq("@tabbox").outerHeight(), jq(".z-tabpanels").outerHeight());
		Assert.assertTrue(jq(".z-tab").eq(2).isVisible());
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(jq("@tabbox").outerHeight(), jq(".z-tabpanels").outerHeight());
		Assert.assertFalse(jq(".z-tab").eq(2).isVisible());
	}
}
