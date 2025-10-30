/* B50_2914010Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 17:44:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2914010Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery btn = jq("@button");
		new Actions(driver).moveToElement(driver.findElement(btn)).perform();

		click(btn);
		waitResponse();

		int windowScrollTop = parseInt(getEval("window.pageYOffset"));
		assertThat(jq("@window").positionTop(), greaterThan(windowScrollTop));
	}
}
