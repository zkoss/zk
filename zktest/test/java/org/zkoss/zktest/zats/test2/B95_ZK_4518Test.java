/* B95_ZK_4518Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 28 16:34:28 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B95_ZK_4518Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery box = jq("@decimalbox");
		click(box);
		waitResponse();

		sendKeys(box, "-3%");
		click(jq("@button"));
		waitResponse();

		Assert.assertFalse("Shouldn't have an invalid message", hasError());
	}
}
