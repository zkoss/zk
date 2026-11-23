/* B85_ZK_3911Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 14 10:42:59 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3911Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery label = jq(".lb");

		Dimension size = driver.manage().window().getSize();
		waitResponse();
		Assert.assertEquals("desktop", label.text());

		driver.manage().window().setSize(new Dimension(768, size.height));
		waitResponse();
		Assert.assertEquals("tablet", label.text());

		driver.manage().window().setSize(new Dimension(414, size.height));
		waitResponse();
		Assert.assertEquals("mobile", label.text());
	}
}
