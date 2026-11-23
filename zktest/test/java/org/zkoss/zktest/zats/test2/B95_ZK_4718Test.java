/* B95_ZK_4718Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Nov 16 12:38:28 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4718Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
	}
}
