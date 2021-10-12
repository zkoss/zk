/* B70_ZK_2255Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 25 12:11:48 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2255Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(size.width / 2, size.height));
		waitResponse();
		JQuery horScrollbar = jq("@listbox .z-scrollbar.z-scrollbar-horizontal");
		int scrollbarWidth = horScrollbar.find(".z-scrollbar-wrapper").width();

		driver.manage().window().setSize(new Dimension(size.width / 4 * 3, size.height));
		waitResponse();
		Assert.assertThat(horScrollbar.find(".z-scrollbar-wrapper").width(), greaterThan(scrollbarWidth));
	}
}
