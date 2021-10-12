/* B86_ZK_4270Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 16 15:13:08 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4270Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(2));
		waitResponse();
		Assert.assertTrue(hasError());
		click(jq(".z-icon-times"));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		click(jq("@button").eq(2));
		waitResponse();
		Assert.assertTrue(jq(".z-toolbar").exists());
		click(jq("@button").eq(0));
		waitResponse();
		Assert.assertTrue(hasError());
		Assert.assertFalse(isZKLogAvailable());
	}
}
