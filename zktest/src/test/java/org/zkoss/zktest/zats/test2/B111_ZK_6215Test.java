/* B111_ZK_6215Test.java

	Purpose:

	Description:

	History:
		Tue Nov 03 10:50:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B111_ZK_6215Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		dragKnob(0, 4, "10.2");
		dragKnob(4, 5, "10.25");
		dragKnob(5, 22, "11.1");
	}

	@Test
	public void testPageIncrement() {
		connect();
		getActions().moveToElement(toElement(jq("$pageSlider")))
				.moveByOffset(50, 0)
				.click()
				.perform();
		waitResponse();
		Assertions.assertEquals("11.25", jq("$clickPos").text());
	}

	@Test
	public void testMinposDigits() {
		connect();
		drag(jq("$minposSlider"), 2, (1.01 - 0.01) / 0.1);
		Assertions.assertEquals("0.21", jq("$minposPos").text());
	}

	@Test
	@Tag("WcagTestOnly")
	public void testKeyboardMinposDigits() {
		connect();
		eval("jq('$keySlider')[0].focus()");
		getActions().sendKeys(Keys.ARROW_RIGHT).sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		Assertions.assertEquals("2.25", jq("$keySlider").attr("aria-valuenow"));
	}

	@Test
	public void testFractionalOffset() {
		connect();
		JQuery slider = jq("$fracSlider");
		drag(slider, 1, 1.05 / 0.1);
		Assertions.assertEquals("0.1", jq("$fracPos").text());
	}

	private void dragKnob(int fromStep, int toStep, String expected) {
		drag(jq("@slider:eq(0)"), toStep - fromStep, (12 - 10) / 0.05);
		Assertions.assertEquals(expected, jq("$scrollingPos").text());
		Assertions.assertEquals(expected, jq("$scrollPos").text());
	}

	private void drag(JQuery slider, int steps, double totalSteps) {
		JQuery btn = slider.find(".z-slider-button");
		double stepInPx = (slider.innerWidth() - btn.outerWidth()) / totalSteps;
		getActions().clickAndHold(toElement(btn))
				.moveByOffset((int) Math.round(steps * stepInPx), 0)
				.release()
				.perform();
		waitResponse();
	}
}
