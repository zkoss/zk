/* B50_ZK_745Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 26 12:04:13 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_745Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals(2, jq("input").length());
		Assert.assertEquals("<foo>1</foo>", jq("input").eq(0).val());
		Assert.assertEquals("<foo>1</foo>", jq("input").eq(1).val());
	}
}
