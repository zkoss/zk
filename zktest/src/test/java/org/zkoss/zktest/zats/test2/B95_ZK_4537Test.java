/* B95_ZK_4537Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 21 16:47:36 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4537Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		Assert.assertFalse(hasError());
	}
}
