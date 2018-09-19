/* B86_ZK_4061Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 19 11:09:58 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4061Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		click(jq("@button"));
		waitResponse();

		driver.navigate().back();
		waitResponse();

		Assert.assertEquals("1", getZKLog());
	}
}
