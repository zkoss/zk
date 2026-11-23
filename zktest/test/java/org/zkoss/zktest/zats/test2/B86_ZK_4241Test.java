/* B86_ZK_4241Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 15 14:37:39 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4241Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		int offsetTop = jq("@popup").offsetTop();

		jq(".z-listbox-body").scrollTop(3000);
		waitResponse();

		Assert.assertEquals("The position of popup is changed", offsetTop, jq("@popup").offsetTop(), 1);
	}
}
