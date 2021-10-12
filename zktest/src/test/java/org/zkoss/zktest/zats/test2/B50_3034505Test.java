/* B50_3034505Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 25 11:55:37 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3034505Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("popup positiopn is correct", jq("$msg").text());
	}
}
