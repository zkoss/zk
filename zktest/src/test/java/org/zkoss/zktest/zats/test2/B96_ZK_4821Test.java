/* B96_ZK_4821Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 19 16:32:19 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B96_ZK_4821Test extends WebDriverTestCase {
	@Test
	public void clickOnBorderTest() {
		Actions act = new Actions(connect());

		for (int i = 0; i < 10; i++) {
			clickOnBorderTest(jq("@checkbox").eq(i), act);
		}
	}

	@Test
	public void clickOnLabelTest() {
		connect();

		for (int i = 0; i < 10; i++) {
			clickOnPartTest(jq(".z-checkbox-content").eq(i), true);
		}
	}

	@Test
	public void clickOnInputTest() {
		connect();

		for (int i = 0; i < 10; i++) {
			clickOnPartTest(jq(".z-checkbox > input").eq(i), false);
		}
	}

	private void clickOnBorderTest(JQuery checkbox, Actions act) {
		String state = checkbox.toWidget().get("state");
		act.moveToElement(toElement(checkbox), 0, checkbox.innerHeight() / 2).click().perform();
		waitResponse();
		Assertions.assertEquals(state, checkbox.toWidget().get("state"), "state shall not change while click on border");
	}

	private void clickOnPartTest(JQuery checkboxPart, boolean onContent) {
		Widget checkbox = checkboxPart.toWidget();
		String state = checkbox.get("state");
		String disabled = checkbox.get("disabled");
		String mold = checkbox.get("mold");
		if (!onContent && ("switch".equals(mold) || "toggle".equals(mold)))
			checkboxPart = jq(checkbox.$n("mold")); // click on switch or toggle instead input
		click(checkboxPart);
		waitResponse();
		if ("true".equals(disabled))
			Assertions.assertEquals(state, checkbox.get("state"), "state shall not change while click on disabled checkbox");
		else
			Assertions.assertNotEquals(state, checkbox.get("state"), "state shall change while click on normal checkbox");
	}
}
