/* B95_ZK_4453Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 11 12:30:04 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B95_ZK_4453Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery intbox = jq("@intbox");
		click(intbox);
		waitResponse();
		sendKeys(intbox, "-1", Keys.TAB);
		waitResponse();
		Assert.assertTrue(getZKLog().startsWith("error: "));
		closeZKLog();

		click(intbox);
		waitResponse();
		sendKeys(intbox, Keys.BACK_SPACE, Keys.BACK_SPACE, "1", Keys.TAB);
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
