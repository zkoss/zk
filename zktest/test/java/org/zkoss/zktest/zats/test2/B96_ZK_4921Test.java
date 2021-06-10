/* B96_ZK_4872Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 08 11:30:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.support.ui.Select;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4921Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("group 1-option 1", jq(":selected").text());
		selectTextAndVerify("group 1-option 3");
		selectTextAndVerify("group 1-option 1");
	}

	private void selectTextAndVerify(String nextSelectText) {
		new Select(toElement(jq("@select"))).selectByVisibleText(nextSelectText);
		waitResponse();
		assertEquals(nextSelectText, jq(":selected").text());
		assertEquals(nextSelectText, getZKLog());
		closeZKLog();
	}
}
