/* B110_ZK_6253Test.java

	Purpose:

	Description:

	History:
		Thu Nov 05 17:10:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6253Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		try {
			switchTheme("iceblue");
			checkSpinButtons("iceblue");
			switchTheme("iceblue_c");
			checkSpinButtons("iceblue_c");
		} finally {
			click(jq("$default"));
			waitResponse();
		}
	}

	private void switchTheme(String theme) {
		click(jq("$" + theme));
		waitResponse();
		connect();
	}

	private void checkSpinButtons(String theme) {
		click(jq(".z-daterangebox-button"));
		waitResponse();
		Assertions.assertEquals(3, jq(".z-timebox").length(), "precondition: the standalone timebox and the popup's two");
		for (int i = 0; i < 3; i++) {
			JQuery timebox = jq(".z-timebox").eq(i);
			// only iceblue_c overflowed: each arrow took half the button's height, borders included
			Assertions.assertTrue(timebox.find(".z-timebox-up").outerHeight() + timebox.find(".z-timebox-down").outerHeight()
					<= timebox.find(".z-timebox-button").innerHeight(), theme + " timebox " + i + ": the arrows do not fit in the spin button");
			JQuery input = timebox.find(".z-timebox-input");
			String radius = input.css("border-top-right-radius");
			String clip = input.css("clip-path");
			Assertions.assertTrue("0px".equals(radius) || clip.startsWith("inset(") && radius.equals(clip.split(" ")[1]),
					theme + " timebox " + i + ": the input's rounded right corners show under the spin button (" + radius + ", " + clip + ")");
		}
	}
}
