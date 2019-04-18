/* F70_ZK_1921Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 10:14:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_1921Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals("image/*", jq(".z-upload input[type=\"file\"]").attr("accept"));
	}
}
