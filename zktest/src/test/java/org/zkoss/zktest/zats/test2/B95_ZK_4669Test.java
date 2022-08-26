/* B95_ZK_4669Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 22 11:50:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4669Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int originalSize = jq(".z-splitter-outer").width();
		WebDriver.Window window = getWebDriver().manage().window();
		window.setSize(new Dimension(400, 800));
		waitResponse();
		checkSize(originalSize);

		window.setSize(new Dimension(1000, 800));
		waitResponse();

		checkSize(originalSize);

		window.setSize(new Dimension(1100, 800));
		waitResponse();
		checkSize(originalSize);

		window.setSize(new Dimension(1100, 600));
		waitResponse();
		checkSize(originalSize);

		window.setSize(new Dimension(1100, 400));
		waitResponse();
		checkSize(originalSize);
	}

	private void checkSize(int originalSize) {
		assertEquals(originalSize, jq(".z-splitter-outer").width(), 1);
		assertEquals(jq(".z-div:eq(0)").width(), jq(".z-hbox").width(), 1);
	}
}
