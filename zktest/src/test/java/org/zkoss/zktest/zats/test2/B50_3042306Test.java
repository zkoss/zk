/* B50_3042306Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 17:38:59 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3042306Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		driver.manage().window().setSize(new Dimension(960, 1080));
		waitResponse();

		Assertions.assertFalse(Boolean.valueOf(getEval("document.body.scrollHeight > document.body.clientHeight")), "Has vScroll");
		Assertions.assertFalse(Boolean.valueOf(getEval("document.body.scrollWidth > document.body.clientWidth")), "Has hScroll");

		driver.manage().window().setSize(new Dimension(300, 1080));
		waitResponse();
		driver.manage().window().setSize(new Dimension(1080, 1080));
		waitResponse();

		Assertions.assertEquals(jq("body").width(), jq("@div").width(), 2);
		Assertions.assertEquals(jq("body").height(), jq("@div").height() + jq("@div").offsetTop(), 2);
	}
}
