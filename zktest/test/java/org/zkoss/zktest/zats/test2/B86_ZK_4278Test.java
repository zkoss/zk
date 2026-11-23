/* B86_ZK_4278Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 17 10:37:06 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4278Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq(".z-panel-expand"));
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
	}
}
