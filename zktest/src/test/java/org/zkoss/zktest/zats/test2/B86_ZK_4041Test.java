/* B86_ZK_4041Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 30 16:15:14 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4041Test extends WebDriverTestCase {

	@Test
	public void test() {
		WebDriver driver = connect();
		JQuery menu = jq(".z-menu");
		Point menuLocation = toElement(menu).getLocation();

		click(menu);
		waitResponse();
		Assertions.assertTrue(menuLocation.getY() < getMenupopupLocation().getY());

		click(jq(".z-button"));
		waitResponse();
		click(menu);
		waitResponse();
		Assertions.assertTrue(menuLocation.getX() < getMenupopupLocation().getX());
		click(jq("body"));
		waitResponse();

		WebDriver.Window window = driver.manage().window();
		window.setSize(new Dimension(400, window.getSize().getHeight()));
		waitResponse();

		click(menu);
		waitResponse();
		Assertions.assertTrue(menuLocation.getX() > getMenupopupLocation().getX());
	}

	private Point getMenupopupLocation() {
		return toElement(jq(".z-menupopup")).getLocation();
	}
}
