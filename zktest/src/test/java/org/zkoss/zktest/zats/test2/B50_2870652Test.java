/* B50_2870652Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 15:38:10 CST 2019, Created by rudyhuang

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
public class B50_2870652Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		checkButtonFlex();

		driver.manage().window().setSize(new Dimension(1024,1080));
		waitResponse();
		checkButtonFlex();

		driver.manage().window().setSize(new Dimension(1280,1080));
		waitResponse();
		checkButtonFlex();
	}

	private void checkButtonFlex() {
		int widthBtn1 = jq("@button:eq(0)").outerWidth();
		int widthBtn2 = jq("@button:eq(1)").outerWidth();
		Assertions.assertEquals(widthBtn1 * 2.0, widthBtn2, 2);

		int topBtn1 = jq("@button:eq(0)").positionTop();
		int topBtn2 = jq("@button:eq(1)").positionTop();
		Assertions.assertEquals(topBtn1, topBtn2);

		int widthBtn3 = jq("@button:eq(2)").outerWidth();
		int widthBtn4 = jq("@button:eq(3)").outerWidth();
		Assertions.assertEquals(widthBtn3 * 2.0, widthBtn4, 2);

		int topBtn3 = jq("@button:eq(2)").positionTop();
		int topBtn4 = jq("@button:eq(3)").positionTop();
		Assertions.assertEquals(topBtn3, topBtn4);
	}
}
