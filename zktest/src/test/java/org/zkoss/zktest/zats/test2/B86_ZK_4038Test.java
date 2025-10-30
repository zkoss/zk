/* B86_ZK_4038Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 14:39:43 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4038Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		int startHeightSum = jq(".z-panel").eq(0).height() + jq(".z-panel").eq(1).height();
		act.dragAndDropBy(toElement(jq(".z-splitter")), -100, 0).perform();
		waitResponse();
		Assertions.assertEquals(startHeightSum, jq(".z-panel").eq(0).height() + jq(".z-panel").eq(1).height());
	}
}
