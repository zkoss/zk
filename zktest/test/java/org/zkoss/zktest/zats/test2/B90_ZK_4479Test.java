/* B90_ZK_4479Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Mar 10 15:34:49 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.ClientWidget;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B90_ZK_4479Test extends WebDriverTestCase {
	@Test
	public void test() {
		String testString = "aaaaaaaaaaaaaaaaaaaaaaa";
		connect();
		
		JQuery cb0 = jq("@chosenbox input").eq(0);
		typeWithoutBlur(cb0, testString);
		waitResponse(true);
		Assert.assertThat(cb0.outerWidth(), allOf(greaterThan(50), lessThan(jq("@chosenbox").eq(0).width())));
		
		JQuery cb1 = jq("@chosenbox input").eq(1);
		typeWithoutBlur(cb1, testString);
		waitResponse(true);
		Assert.assertThat(cb1.outerWidth(), allOf(greaterThan(50), lessThan(jq("@chosenbox").eq(1).width())));
	}
	
	private void typeWithoutBlur(ClientWidget locator, String text) {
		focus(locator);
		WebElement webElement = toElement(locator);
		webElement.clear();
		webElement.sendKeys(text);
	}
}
