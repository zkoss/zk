/* B86_ZK_4182Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 09 16:29:44 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4182Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery errbox = jq("@errorbox:last");
		click(jq("@button:eq(0)"));
		waitResponse(true);
		Assert.assertFalse("The last errorbox shouldn't be visible", errbox.isVisible());

		click(jq("@button:eq(1)"));
		waitResponse(true);
		Assert.assertFalse("The last errorbox shouldn't be visible", errbox.isVisible());
	}
}
