/* B86_ZK_4041Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 30 16:15:14 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4041Test extends WebDriverTestCase {

	@Test
	public void test() {
		WebDriver driver = connect();
		JQuery menu = jq(".z-menu");
		Point menuLocation = toElement(menu).getLocation();

		click(menu);
		waitResponse();
		Assert.assertTrue(menuLocation.getY() < getMenupopupLocation().getY());

		click(jq(".z-button"));
		waitResponse();
		click(menu);
		waitResponse();
		Assert.assertTrue(menuLocation.getX() < getMenupopupLocation().getX());
		click(jq("body"));
		waitResponse();

		WebDriver.Window window = driver.manage().window();
		window.setSize(new Dimension(400, window.getSize().getHeight()));
		waitResponse();

		click(menu);
		waitResponse();
		Assert.assertTrue(menuLocation.getX() > getMenupopupLocation().getX());
	}

	private Point getMenupopupLocation() {
		return toElement(jq(".z-menupopup")).getLocation();
	}
}
