/* B50_3039282Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 16:44:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3039282Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertFalse(hasError());

		int lengthBefore = jq("@row").length();
		click(jq("@button"));
		waitResponse();
		int lengthAfter = jq("@row").length();
		Assert.assertEquals(lengthBefore, lengthAfter);
	}
}
