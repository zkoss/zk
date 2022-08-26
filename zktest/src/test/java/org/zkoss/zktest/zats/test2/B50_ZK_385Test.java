/* B50_ZK_385Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 12:30:51 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_385Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		int[] startD = {driver.manage().window().getSize().width ,driver.manage().window().getSize().height};
		int startPosLeft = jq(".z-window").positionLeft();
		
		act.dragAndDropBy(toElement(jq(".z-window-header")), 200, 200).build().perform();
		Dimension d = new Dimension(1280, startD[1]);
		driver.manage().window().setSize(d);
		Assertions.assertEquals(startPosLeft + 200, jq(".z-window").positionLeft());

		d = new Dimension(startD[0], startD[1]);
		driver.manage().window().setSize(d);
	}
}
