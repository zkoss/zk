/* F60_ZK_951Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 15 15:28:17 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F60_ZK_951Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		new Select(toElement(jq("@selectbox:eq(0)"))).selectByVisibleText("BigData");
		waitResponse();
		new Select(toElement(jq("@selectbox:eq(1)"))).selectByVisibleText("2");
		waitResponse();
		new Select(toElement(jq("@selectbox:eq(2)"))).selectByVisibleText("10");
		waitResponse();
		new Select(toElement(jq("@selectbox:eq(3)"))).selectByVisibleText("10");
		waitResponse();
		Assert.assertEquals(10, jq(".z-biglistbox-body-frozen .z-biglistbox-row").length());

		Actions actions = getActions();
		actions.clickAndHold(toElement(jq(".z-biglistbox-wscroll-horizontal .z-biglistbox-wscroll-body")))
				.moveByOffset(200, 0)
				.pause(1500)
				.release()
				.perform();
		waitResponse();
		Assert.assertNotEquals("Header x = 2", trim(jq(".z-biglistbox-header-leftmost:last").text()));

		actions.clickAndHold(toElement(jq(".z-biglistbox-wscroll-vertical .z-biglistbox-wscroll-body")))
				.moveByOffset(0, 200)
				.pause(1500)
				.release()
				.perform();
		waitResponse();
		Assert.assertNotEquals("y = 0", trim(jq(".z-biglistbox-row:first td:first").text()));

		click(jq("@button"));
		waitResponse();
		Assert.assertFalse(hasError());
	}
}
