/* B86_ZK_4303Test.java

	Purpose:
		
	Description:
		
	History:
		Mon May 27 14:13:53 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4303Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertTrue("Expected to see #2", jq("@div @label:contains(in contents #2)").exists());
	}
}
