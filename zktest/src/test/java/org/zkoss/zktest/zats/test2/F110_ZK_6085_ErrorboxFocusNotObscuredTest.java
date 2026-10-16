/* F110_ZK_6085_ErrorboxFocusNotObscuredTest.java

	Purpose:

	Description:

	History:
		Thu Oct 01 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class F110_ZK_6085_ErrorboxFocusNotObscuredTest extends WebDriverTestCase {
	@Test
	public void clientConstraint() {
		connect();
		click(jq("$client"));
		waitResponse();
		// sample every frame of the error box's fade-in, which is too short to poll from here
		eval("(function () { var n = zk.$('$client1').$n(), s = window.fadeIn = {frames: 0, covered: 0};"
				+ " (function tick() { var eb = jq('.z-errorbox')[0], r = n.getBoundingClientRect();"
				+ " if (eb && zk.animating()) { s.frames++;"
				+ " if (eb.contains(document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2))) s.covered++; }"
				+ " if (!s.frames || zk.animating()) requestAnimationFrame(tick); })(); })()");
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse(true);
		Assertions.assertTrue(Integer.parseInt(getEval("window.fadeIn.frames")) > 0, "The error box should fade in, so its frames can be sampled");
		Assertions.assertEquals("0", getEval("window.fadeIn.covered"), "No frame of the fade-in should cover the focused textbox");
		assertNotObscured("client1");
	}

	@Test
	public void serverValidation() {
		connect();
		click(jq("$server"));
		waitResponse();
		getActions().sendKeys("x", Keys.TAB).perform();
		waitResponse(true);
		assertNotObscured("server1");
	}

	@Test
	public void scrollableContainer() {
		connect();
		click(jq("$scroll"));
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse(true);
		jq("$scroller").scrollTop(5);
		waitResponse(true);
		assertNotObscured("scroll1");
	}

	private void assertNotObscured(String id) {
		Assertions.assertTrue(jq("@errorbox").isVisible(), "precondition: an error box is open");
		Assertions.assertTrue(jq("$" + id).is(":focus"), "precondition: Tab moved the focus to " + id);
		Assertions.assertTrue(Boolean.parseBoolean(getEval("(function (n) { var r = n.getBoundingClientRect();"
				+ " return n.contains(document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2)); })"
				+ "(zk.$('$" + id + "').$n())")), "The error box should not cover the focused " + id);
	}
}
