/* B104_ZK_6078Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Mar 25 16:22:36 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_6078Test extends WebDriverTestCase {
	@Test
	public void test() {
        connect();

        // Select the first employee (Max Mustermann) — transition from null to object
		JQuery firstRow = jq("@listitem").eq(0);
		click(firstRow);
		waitResponse();

        JQuery firstNameBox = jq("$firstName");
		JQuery lastNameBox = jq("$lastName");

		// After selecting from null, form proxy should update the textboxes
		assertEquals("Max", firstNameBox.val(), "First name should update after selection");
		assertEquals("Mustermann", lastNameBox.val(), "Last name should update after selection");

		// Select the second employee (Erika Musterfrau)
		JQuery secondRow = jq("@listitem").eq(1);
		click(secondRow);
		waitResponse();

		assertEquals("Erika", firstNameBox.val(), "First name should update to second employee");
		assertEquals("Musterfrau", lastNameBox.val(), "Last name should update to second employee");
	}
}
