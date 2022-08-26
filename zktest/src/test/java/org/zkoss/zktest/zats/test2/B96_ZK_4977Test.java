/* B96_ZK_4977Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 28 16:36:59 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4977Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery label = jq(".lb");

		Dimension size = driver.manage().window().getSize();
		waitResponse();
		Assertions.assertEquals("desktop", label.text());

		driver.manage().window().setSize(new Dimension(768, size.height));
		waitResponse();
		Assertions.assertEquals("tablet", label.text());

		driver.manage().window().setSize(new Dimension(414, size.height));
		waitResponse();
		Assertions.assertEquals("mobile", label.text());
	}
}
