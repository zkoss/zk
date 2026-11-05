/* B111_ZK_6258Test.java

		Purpose:

		Description:

		History:
				Thu Nov 05 12:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B111_ZK_6258Test extends WebDriverTestCase {

	private String selection() {
		return getEval("(function () {var s = zk.Widget.$('$cr')._jcrop.tellSelect();"
				+ "return [s.x, s.y, s.w, s.h].map(Math.round).join(',');})()");
	}

	private String coords() {
		click(jq("$log"));
		waitResponse();
		return jq("$coordLog").text();
	}

	private void connectAndWaitForJcrop() {
		connect();
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(d -> Boolean.parseBoolean(getEval("!!zk.Widget.$('$cr')._jcrop")));
	}

	@Test
	public void test() {
		connectAndWaitForJcrop();
		click(jq("$place"));
		waitResponse();
		assertEquals("0,0,200,100", selection());
		assertEquals("0,0,200,100", coords());

		click(jq("$shift"));
		waitResponse();
		assertEquals("100,0,100,100", selection());
		assertEquals("100,0,100,100", coords());
	}

	@Test
	public void responsePausedByMount() {
		connectAndWaitForJcrop();
		assertFalse(Boolean.parseBoolean(getEval("zk.isLoaded('zul.grid')")));
		click(jq("$mount"));
		waitResponse();
		assertEquals("0,0,200,100", selection());
		assertEquals("0,0,200,100", coords());
	}

	@Test
	public void clientSetters() {
		connectAndWaitForJcrop();
		eval("zk.Widget.$('$cr').setW(200).setX(0)");
		waitResponse();
		assertEquals("0,0,200,100", selection());
		assertEquals("150,0,50,100", coords());
	}
}
