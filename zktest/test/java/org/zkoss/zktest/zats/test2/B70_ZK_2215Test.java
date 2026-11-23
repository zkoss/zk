/* B70_ZK_2215Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 26 17:57:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2215Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		Assert.assertEquals("image/*", jq(".z-upload input[type=\"file\"]").attr("accept"));
	}
}
