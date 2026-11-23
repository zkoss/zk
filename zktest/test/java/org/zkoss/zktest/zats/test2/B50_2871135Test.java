/* B50_2871135Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 14:58:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2871135Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		rightClick(jq("@label"));
		sleep(1000); // can't use waitResponse since the loading mask might not go away if this bug exists

		Assert.assertTrue(jq("@popup").isVisible());
		Assert.assertFalse(jq(".z-apply-mask").exists());
	}
}
