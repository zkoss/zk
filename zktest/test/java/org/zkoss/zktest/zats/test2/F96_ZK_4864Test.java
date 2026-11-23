/* F96_ZK_4864Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 23 10:36:41 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.Select;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class F96_ZK_4864Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		// Step 1
		checkDateboxPopup(0, true);

		// Step 2
		checkDateboxPopup(1, false);

		// Step 3
		click(jq("@button:eq(0)"));
		waitResponse();
		checkDateboxPopup(0, false);

		// Step 4
		click(jq("@button:eq(1)"));
		waitResponse();
		checkDateboxPopup(0, true);
	}

	private void checkDateboxPopup(int dateboxIndex, boolean shallClosePopup) {
		click(jq(".z-datebox-button").eq(dateboxIndex));
		waitResponse();
		Select sb = new Select(toElement(jq(".z-datebox-popup.z-datebox-open .z-datebox-timezone>select")));
		sb.selectByVisibleText("GMT+12:00".equals(sb.getFirstSelectedOption().getText()) ? "GMT+08:00" : "GMT+12:00");
		waitResponse();
		boolean popupIsOpen = jq(".z-datebox-popup.z-datebox-open").exists();
		if (shallClosePopup)
			Assert.assertFalse("the datebox popup shall close", popupIsOpen);
		else
			Assert.assertTrue("the datebox popup shall not close", popupIsOpen);
	}
}
