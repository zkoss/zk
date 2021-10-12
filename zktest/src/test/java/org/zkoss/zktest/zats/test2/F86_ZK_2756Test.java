/* F86_ZK_2756Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 03 17:23:52 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_2756Test extends WebDriverTestCase {
	@Test
	public void test() {
		WebDriver driver = connect();
		Assert.assertEquals(3, jq("@select > optgroup").length());

		Select select = new Select(driver.findElement(jq("@select").toBy()));
		select.selectByVisibleText("2015-Q4");
		waitResponse();

		click(jq("@button:contains(mold=default)"));
		waitResponse();
		Assert.assertTrue(jq("@listitem:contains(2015-Q4)").is(".z-listitem-selected"));

		click(jq("@listgroup > .z-listgroup-icon").eq(0));
		waitResponse();
		click(jq("@button:contains(mold=select)"));
		waitResponse();
		Assert.assertEquals(0, jq("@select > optgroup:eq(0)").find("option").length());
	}
}
