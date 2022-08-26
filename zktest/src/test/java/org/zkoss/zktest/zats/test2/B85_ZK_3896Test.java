/* B85_ZK_3896Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Mar 22 12:55:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3896Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		for (JQuery datebox : jq(".z-datebox"))
			dateChangeTest(datebox);
	}

	private void dateChangeTest(JQuery datebox) {
		String oldDate = datebox.find(".z-datebox-input").val();

		click(datebox.find(".z-datebox-button"));
		waitResponse(true);

		JQuery today = jq("div.z-datebox-popup.z-datebox-open")
				.find("td.z-calendar-cell.z-calendar-selected");
		JQuery prevSibling = today.prev();
		click(prevSibling.exists() ? prevSibling : today.next());
		waitResponse(true);

		Assertions.assertFalse(jq("div.z-errorbox.z-errorbox-open").exists());
		Assertions.assertNotEquals(oldDate, datebox.find(".z-datebox-input").val());
	}
}
