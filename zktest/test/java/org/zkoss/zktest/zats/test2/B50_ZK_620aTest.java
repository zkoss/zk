/* B50_ZK_620aTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 22 16:16:41 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_620aTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
		click(jq("@tab:contains(Discovery)"));
		waitResponse();
		String zklog = getZKLog();
		Assert.assertTrue(zklog.contains("beforeSize"));
		Assert.assertTrue(zklog.contains("onFitSize"));
		Assert.assertTrue(zklog.contains("onSize"));
	}
}
