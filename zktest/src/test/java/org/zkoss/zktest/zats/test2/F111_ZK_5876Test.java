/* F111_ZK_5876Test.java

		Purpose:

		Description:

		History:
				Wed May 13 17:12:43 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F111_ZK_5876Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();
		assertLooksEditable();

		JQuery input = jq("$cbReadonly").find("input");
		click(input);
		waitResponse(true);
		assertEquals("true", getEval("zk.Widget.$('$cbReadonly').isOpen()"));

		sendKeys(input, Keys.ARROW_RIGHT);
		waitResponse();
		assertEquals("Apple", input.val());

		try {
			click(jq("$iceblue"));
			waitResponse();
			connect();
			assertLooksEditable();
		} finally {
			click(jq("$default"));
			waitResponse();
		}
	}

	private void assertLooksEditable() {
		String readonlyStyle = "getComputedStyle(zk.Widget.$('$cbReadonly').getInputNode())";
		assertEquals("rgba(0, 0, 0, 0)", getEval(readonlyStyle + ".caretColor"));
		assertEquals(getEval("getComputedStyle(zk.Widget.$('$cbDefault').getInputNode()).backgroundColor"),
				getEval(readonlyStyle + ".backgroundColor"));
	}
}
