/* B50_ZK_699_ListgroupTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 17:38:19 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_699_ListgroupTest extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq(".z-listgroup-icon").eq(0));
			waitResponse();
			click(jq("@button"));
			waitResponse();
			click(jq(".z-listgroup-icon").eq(0));
			waitResponse();
			Assert.assertFalse(isZKLogAvailable());
		} catch (Exception e) {
			Assert.fail();
		}
	}
}

