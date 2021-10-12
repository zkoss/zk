/* B50_ZK_699Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 17:14:24 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_699Test extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq(".z-group-icon").eq(0));
			waitResponse();
			click(jq("@button"));
			waitResponse();
			click(jq(".z-group-icon").eq(0));
			waitResponse();
			Assert.assertFalse(isZKLogAvailable());
		} catch (Exception e) {
			Assert.fail();
		}
	}
}
