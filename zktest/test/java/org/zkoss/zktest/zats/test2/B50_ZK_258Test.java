/* B50_ZK_258Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 12:05:15 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_258Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertTrue(jq(".z-tab").eq(16).hasClass("z-tab-selected"));
		Assert.assertEquals("true", getZKLog());
	}
}
