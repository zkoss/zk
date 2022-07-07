/* B96_ZK_4821Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 19 16:32:19 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

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
		Assert.assertEquals("state shall not change while click on border", state, checkbox.toWidget().get("state"));
	}

	private void clickOnPartTest(JQuery checkboxPart, boolean onContent) {
		Widget checkbox = checkboxPart.toWidget();
		String state = checkbox.get("state");
		boolean disabled = checkbox.is("disabled");
		String mold = checkbox.get("mold");
		if (!onContent && ("switch".equals(mold) || "toggle".equals(mold)))
			checkboxPart = jq(checkbox.$n("mold")); // click on switch or toggle instead input
		click(checkboxPart);
		waitResponse();
		if (disabled)
			Assert.assertEquals("state shall not change while click on disabled checkbox", state, checkbox.get("state"));
		else
			Assert.assertNotEquals("state shall change while click on normal checkbox", state, checkbox.get("state"));
	}
}
