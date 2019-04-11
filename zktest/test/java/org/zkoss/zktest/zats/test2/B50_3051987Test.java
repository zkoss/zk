/* B50_3051987Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 26 15:34:04 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3051987Test extends WebDriverTestCase {
	@Test
	public void testReadonly() {
		connect();
		checkInputAndButtonBorder();

		click(jq("@button:contains(readonly)"));
		waitResponse();
		checkInputAndButtonBorder();
	}

	@Test
	public void testButtonVisible() {
		connect();

		click(jq("@button:contains(buttonVisible)"));
		waitResponse();

		boolean hasInputBorder = !"0px".equals(jq("@combobox input").css("border-right-width"));
		Assert.assertTrue("Should have border on input right", hasInputBorder);
	}

	private void checkInputAndButtonBorder() {
		boolean hasInputBorder = !"0px".equals(jq("@combobox input").css("border-right-width"));
		boolean hasButtonBorder = !"0px".equals(jq("@combobox a").css("border-left-width"));
		Assert.assertNotEquals("Both two have border", hasButtonBorder, hasInputBorder);
	}
}
