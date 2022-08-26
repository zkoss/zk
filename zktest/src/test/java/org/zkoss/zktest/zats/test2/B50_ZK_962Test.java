/* B50_ZK_962Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 24 15:03:27 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_962Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("w3c", false); // drag&drag workaround
	}

	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		Assertions.assertEquals("Panel 1Panel 2Panel 3Panel 5", jq(".z-portalchildren-content").eq(0).children().text());
		Assertions.assertEquals("Panel 4", jq(".z-portalchildren-content").eq(1).children().text());
		
		click(jq("@button:contains(single column)"));
		waitResponse();
		act.dragAndDrop(toElement(jq(".z-panel-head:contains(Panel 1)")), toElement(jq(".z-panel-head:contains(Panel 3)"))).perform();
		waitResponse();
		Assertions.assertEquals("Panel 2Panel 1Panel 3Panel 5", jq(".z-portalchildren-content").eq(0).children().text());
		click(jq("@button:contains(two columns)"));
		waitResponse();
		Assertions.assertEquals("Panel 2Panel 1Panel 3Panel 5", jq(".z-portalchildren-content").eq(0).children().text());
		Assertions.assertEquals("Panel 4", jq(".z-portalchildren-content").eq(1).children().text());
	}
}
