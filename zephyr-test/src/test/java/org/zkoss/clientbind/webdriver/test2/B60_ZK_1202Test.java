package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B60_ZK_1202Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(jq(".z-combobox").length(), 2, "You shall see two comboboxes.");
		JQuery btn1 = jq(".z-combobox-button");
		JQuery regular = jq(".z-comboitem:contains(Regular)");
		JQuery working = jq(".z-comboitem:contains(Working)");
		String[] regularDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		String[] workingDays = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

		click(btn1);
		waitResponse();

		assertTrue(regular.exists(), "The first combobox contains the week type: 'Regular Week' and 'Working Week'.");
		assertTrue(working.exists(), "The first combobox contains the week type: 'Regular Week' and 'Working Week'.");

		click(regular);
		waitResponse();
		Assertions.assertFalse(jq(".z-window-modal").exists(), "No exceptions should occur when changing the selection in any order.");

		verifyDayOk("If 'Regular Week' is selected in the first combobox, the second combobox should contain 'Sunday' through 'Saturday'.", regularDays);

		click(btn1);
		waitResponse();

		click(working);
		waitResponse();
		assertFalse(jq(".z-window-modal").exists(), "No exceptions should occur when changing the selection in any order.");

		verifyDayOk("If 'Working Week' is selected in the first combobox, the second combobox should contain 'Monday' through 'Friday'.", workingDays);
	}

	public void verifyDayOk(String msg, String[] days) {
		for (String day : days) {
			click(jq(".z-combobox:eq(1)").toWidget().$n("btn"));
			waitResponse();
			JQuery dayItem = jq(".z-comboitem:contains(" + day + ")");
			assertNotEquals(dayItem.css("display"), "none", msg);
			click(dayItem);
			waitResponse();
			assertFalse(jq(".z-window-modal").exists(), "No exceptions should occur when changing the selection in any order.");
		}
	}
}