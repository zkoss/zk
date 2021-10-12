/* B70_ZK_3014Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 12:43:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_3014Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(widget("@bandbox").$n("btn"));
		waitResponse(true);
		int listboxWidth = jq("@listbox").width();

		click(jq("@button"));
		waitResponse();
		click(widget("@bandbox").$n("btn"));
		waitResponse(true);

		Assert.assertTrue(jq("@listbox").width() < listboxWidth);
	}
}
