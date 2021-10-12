/* B50_ZK_660Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 15:41:09 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_660Test extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq("@button:contains(add tab and tabpanel)"));
			waitResponse();
			click(jq("@button:contains(add tab and tabpanel)"));
			waitResponse();
			click(jq(".z-tab-icon").eq(1));
			waitResponse();
			click(jq("@button:contains(add tab and tabpanel)"));
			waitResponse();
			click(jq("@button:contains(add tab and tabpanel)"));
			waitResponse();
			click(jq("@button:contains(invalidate)"));
			waitResponse();
			click(jq(".z-tab-icon").eq(1));
			waitResponse();
			Assert.assertFalse(isZKLogAvailable());
		} catch (Exception e) {
			Assert.fail();
		}
	}
}
