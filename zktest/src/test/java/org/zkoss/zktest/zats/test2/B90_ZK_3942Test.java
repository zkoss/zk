/* B90_ZK_3942Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 29 15:07:09 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_3942Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertFalse(jq(".z-apply-loading").exists());
		Assert.assertEquals("onCreate\nonClientInfo", getZKLog());
	}
}