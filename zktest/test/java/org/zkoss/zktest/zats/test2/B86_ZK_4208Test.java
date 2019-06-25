/* B86_ZK_4208Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 09 12:02:38 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4208Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
		Assert.assertEquals(1, jq(".z-notification").length());
		click(jq(".z-notification-close"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-notification").length());
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(1, jq(".z-notification").length());
		sleep(5000); // wait for notification close itself automatically
		Assert.assertEquals(0, jq(".z-notification").length());
	}
}
