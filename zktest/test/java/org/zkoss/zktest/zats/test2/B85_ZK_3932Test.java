/* B85_ZK_3932Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 12:36:59 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3932Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
