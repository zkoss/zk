/* F86_ZK_3179Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Aug 29 17:07:03 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_3179Test extends WebDriverTestCase {
	@Test
	public void test() {
		driver = connect();
		JQuery overflowButton = jq(".z-toolbar-overflowpopup-button");

		click(jq("$setWidth200"));
		waitResponse();
		Assert.assertTrue(overflowButton.isVisible());

		click(overflowButton);
		waitResponse();
		Assert.assertTrue(getNumberOfPopupChildren() > 0);
		Assert.assertEquals(6, getNumberOfToolbarChildren() + getNumberOfPopupChildren());

		click(jq("$resetWidth"));
		waitResponse();
		driver.manage().window().maximize();
		waitResponse();
		Assert.assertFalse(overflowButton.isVisible());
		Assert.assertEquals(6, getNumberOfToolbarChildren());
	}

	private int getNumberOfToolbarChildren() {
		return jq(".z-toolbar-content > *").length();
	}

	private int getNumberOfPopupChildren() {
		return jq(".z-toolbar-popup > *").length();
	}
}
