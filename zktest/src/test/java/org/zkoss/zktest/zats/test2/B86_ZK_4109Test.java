/* B86_ZK_4109Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 16:13:54 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4109Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		jq(".z-listbox-body").scrollTop(1000000);
		waitResponse();
		int currentScroll = jq(".z-listbox-body").scrollTop();
		act.moveToElement(toElement(jq(".z-listbox-body")), 100, 300).perform();
		waitResponse();
		act.click().perform();
		waitResponse();
		Assertions.assertEquals(currentScroll, jq(".z-listbox-body").scrollTop());
	}
}
