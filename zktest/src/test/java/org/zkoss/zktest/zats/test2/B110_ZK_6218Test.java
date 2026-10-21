/* B110_ZK_6218Test.java

	Purpose:

	Description:

	History:
		Wed Oct 21 10:30:48 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6218Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$inWindow"));
		waitResponse();
		click(jq("$other"));
		waitResponse(true);
		assertBesideTheField("precondition: the error box opened beside the textbox");
		Assertions.assertTrue(jq("@errorbox").toWidget().is("visible"),
				"precondition: the error box has finished fading in, so a press on the window reaches it");
		click(jq("$win").find(".z-window-header"));
		waitResponse(true);
		assertBesideTheField("A press on the window that holds the textbox should not move its error box");
	}

	private void assertBesideTheField(String message) {
		JQuery field = jq("$inWindow"), box = jq("@errorbox");
		Assertions.assertTrue(box.isVisible(), "precondition: an error box is open");
		Assertions.assertTrue(box.offsetTop() < field.offsetTop() + field.outerHeight()
				&& box.offsetTop() + box.outerHeight() > field.offsetTop(), message);
	}
}
