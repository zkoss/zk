/* B50_2972980Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 10:58:35 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2972980Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		click(jq("@label"));
		waitResponse();
		Assert.assertTrue(jq(".z-messagebox-window").isVisible());
	}
}
