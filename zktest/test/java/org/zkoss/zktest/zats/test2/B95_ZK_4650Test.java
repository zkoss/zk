/* B95_ZK_4650Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Oct 30 16:01:42 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4650Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@imagemap"));
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
	}
}
