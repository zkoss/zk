/* F50_2940704_GridTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 09:49:51 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
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
public class F50_2940704_GridTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery body = jq("@grid .z-grid-body");
		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(size.width, size.height / 10 * 8));
		waitResponse();
		Assertions.assertEquals(body.scrollHeight(), body.height());

		driver.manage().window().setSize(new Dimension(size.width, size.height / 10 * 6));
		waitResponse();
		Assertions.assertEquals(body.scrollHeight(), body.height());

		click(widget("@radio:eq(0)").$n("real"));
		waitResponse();
		Assertions.assertTrue(jq("@paging").offsetTop() < body.offsetTop());

		click(widget("@radio:eq(1)").$n("real"));
		waitResponse();
		Assertions.assertTrue(jq("@paging").offsetTop() > body.offsetTop());

		click(widget("@radio:eq(2)").$n("real"));
		waitResponse();
		Assertions.assertEquals(body.scrollHeight(), body.height());
		Assertions.assertEquals(2, jq(".z-paging").length());
	}
}
