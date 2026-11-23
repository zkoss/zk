/* F50_3025419Test.java

	Purpose:

	Description:

	History:
		Tue Apr 09 12:11:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3025419Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Dimension size = driver.manage().window().getSize();
		int width1 = jq("@listbox").width();
		checkFixedColumnWidth();

		driver.manage().window().setSize(new Dimension(size.width / 2, size.height));
		waitResponse();
		Assert.assertTrue(jq("@listbox").width() < width1);
		checkFixedColumnWidth();

		driver.manage().window().setSize(new Dimension(size.width / 4, size.height));
		waitResponse();
		Assert.assertTrue(jq("@listbox").width() < width1);
		checkFixedColumnWidth();
	}

	private void checkFixedColumnWidth() {
		Assert.assertEquals(150, jq("@listheader:eq(0)").width(), 1);
		Assert.assertEquals(75, jq("@listheader:eq(1)").width(), 1);
		Assert.assertEquals(75, jq("@listheader:eq(2)").width(), 1);
		Assert.assertEquals(300, jq("@listheader:eq(3)").width(), 1);

		Assert.assertEquals(150, jq("@listcell:eq(0)").width(), 1);
		Assert.assertEquals(75, jq("@listcell:eq(1)").width(), 1);
		Assert.assertEquals(75, jq("@listcell:eq(2)").width(), 1);
		Assert.assertEquals(300, jq("@listcell:eq(3)").width(), 1);
	}
}
