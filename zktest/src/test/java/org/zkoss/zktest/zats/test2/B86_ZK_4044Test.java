/* B86_ZK_4044Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 22 12:33:36 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4044Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		zoomReset();
		zoomIn(2);
		click(jq("@button:contains(refresh)"));
		waitResponse();
		zoomOut(1);
		click(jq("@button:contains(scrollRight)"));
		waitResponse();
		Assertions.assertEquals(jq(".z-listheader").eq(8).positionLeft(), jq(".z-listcell").eq(8).positionLeft());
		zoomReset();
		zoomIn(5);
		click(jq("@button:contains(refresh)"));
		waitResponse();
		click(jq("@button:contains(scrollRight)"));
		waitResponse();
		Assertions.assertEquals(jq(".z-listheader").eq(8).positionLeft(), jq(".z-listcell").eq(8).positionLeft());
		zoomReset();
	}

	private void zoomIn(int times) {
		WebElement html = driver.findElement(By.tagName("html"));
		for (int i = 0; i < times; i++) {
			html.sendKeys(Keys.chord(isMac() ? Keys.COMMAND : Keys.CONTROL, Keys.ADD));
		}
	}
	
	private void zoomOut(int times) {
		WebElement html = driver.findElement(By.tagName("html"));
		for (int i = 0; i < times; i++) {
			html.sendKeys(Keys.chord(isMac() ? Keys.COMMAND : Keys.CONTROL, Keys.SUBTRACT));
		}
	}
	
	private void zoomReset() {
		WebElement html = driver.findElement(By.tagName("html"));
		html.sendKeys(Keys.chord(isMac() ? Keys.COMMAND : Keys.CONTROL, "0"));
	}
}
