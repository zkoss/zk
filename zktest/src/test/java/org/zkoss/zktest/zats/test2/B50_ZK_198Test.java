/* B50_ZK_198Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 02 18:06:22 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_198Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		int heightBefore = jq(".z-center .z-center").height();
		WebElement splitter = toElement(jq(".z-east-splitter"));
		act.dragAndDropBy(splitter, 50, 0).build().perform();
		waitResponse();
		Assertions.assertEquals(heightBefore, jq(".z-center .z-center").height());
	}
}
