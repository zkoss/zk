/* F104_ZK_6095Test.java

		Purpose:

		Description:

		History:
				Wed May 13 17:10:17 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_6095Test extends WebDriverTestCase {

	private void pressCtrl(String key) {
		Actions action = getActions();
		action.keyDown(Keys.CONTROL).sendKeys(key).keyUp(Keys.CONTROL).perform();
		waitResponse();
	}

	@Test
	public void test() {
		connect();
		// focus the textbox inside the center region so keydown originates
		// inside the borderlayout's widget subtree
		focus(jq("$tb"));
		waitResponse();

		JQuery result = jq("$result");
		pressCtrl("k");
		assertEquals("ctrlKey:75", result.text());

		pressCtrl("h");
		assertEquals("ctrlKey:72", result.text());

		// negative chord: J is not in ctrlKeys, result must not change
		pressCtrl("j");
		assertEquals("ctrlKey:72", result.text());

		// focus inside north region — chord must still route to the borderlayout
		focus(jq("$tbNorth"));
		waitResponse();
		pressCtrl("k");
		assertEquals("ctrlKey:75", result.text());

		// empty string is normalized to null on the server
		click(jq("$testEmpty"));
		waitResponse();
		assertEquals("true", jq("$emptyResult").text());
	}

	@Test
	public void testPerRegionOverride() {
		connect("/test2/F104-ZK-6095-override.zul");
		focus(jq("$tb"));
		waitResponse();

		pressCtrl("k");
		// the region's handler is closer to focus; the event must not bubble
		// up to the borderlayout
		assertEquals("region", jq("$result").text());
	}

	@Test
	public void testMVVMBinding() {
		connect("/test2/F104-ZK-6095-mvvm.zul");
		// ctrlKeys was @load-bound from the view-model
		assertEquals("^k", jq("$boundCtrlKeys").text());

		focus(jq("$tb"));
		waitResponse();
		pressCtrl("k");
		assertEquals("ctrlKey:75", jq("$result").text());
	}
}
