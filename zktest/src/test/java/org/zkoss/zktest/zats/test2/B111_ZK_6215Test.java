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
		JQuery slider = jq("$minposSlider"), btn = slider.find(".z-slider-button");
		hold(slider, 2, (1.01 - 0.01) / 0.1);
		Assertions.assertEquals("0.21", jq("#zul_slidetip").text());
		getActions().release().perform();
		waitResponse();
		Assertions.assertEquals("0.21", jq("$minposPos").text());
		Assertions.assertEquals("0.21", btn.attr("title"));

		getActions().moveToElement(toElement(slider)).click().perform();
		waitResponse();
		String clickPos = jq("$minposPos").text();
		Assertions.assertNotEquals("0.21", clickPos);
		Assertions.assertEquals(clickPos, btn.attr("title"));
	}

	@Test
	@Tag("WcagTestOnly")
	public void testKeyboardMinposDigits() {
		connect();
		eval("jq('$keySlider')[0].focus()");
		getActions().sendKeys(Keys.ARROW_RIGHT).sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		Assertions.assertEquals("2.25", jq("$keySlider").attr("aria-valuenow"));
		Assertions.assertEquals("2.25", jq("$keySlider").find(".z-slider-button").attr("title"));
	}

	@Test
	public void testMaxposDigits() {
		connect();
		JQuery slider = jq("$maxposSlider");
		hold(slider, 1, 1); // the whole track, to maxpos
		Assertions.assertEquals("1.05", jq("#zul_slidetip").text());
		getActions().release().perform();
		waitResponse();
		Assertions.assertEquals("1.05", jq("$maxposPos").text());
		Assertions.assertEquals("1.05", slider.find(".z-slider-button").attr("title"));
	}

	@Test
	@Tag("WcagTestOnly")
	public void testKeyboardKeepsDigits() {
		connect();
		eval("jq('$maxposSlider')[0].focus()");
		getActions().sendKeys(Keys.END).perform();
		waitResponse();
		Assertions.assertEquals("1.05", jq("$maxposSlider").attr("aria-valuenow"));
		Assertions.assertEquals("1.05", jq("$maxposSlider").find(".z-slider-button").attr("title"));

		eval("jq('$defaultStepSlider')[0].focus()");
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		Assertions.assertEquals("1.3", jq("$defaultStepSlider").attr("aria-valuenow"));
		Assertions.assertEquals("1.3", jq("$defaultStepSlider").find(".z-slider-button").attr("title"));
	}

	@Test
	public void testFractionalOffset() {
		connect();
		JQuery slider = jq("$fracSlider");
		drag(slider, 1, 1.05 / 0.1);
		Assertions.assertEquals("0.1", jq("$fracPos").text());
		Assertions.assertEquals("0.10", slider.find(".z-slider-button").attr("title"));
	}

	@Test
	public void testKnobDigits() {
		connect();
		Assertions.assertEquals("1.05", jq("$knobMaxSlider").find(".z-slider-input").val());
		Assertions.assertEquals("0.01", jq("$knobMinSlider").find(".z-slider-input").val());
	}

	@Test
	public void testKnobInputFitsDigits() {
		connect();
		eval("zk.Widget.$(jq('$knobMinSlider')).setStep(1).setMaxpos(10).setMinpos(0.25).setCurpos(10)");
		waitResponse();
		Assertions.assertEquals("10.00", jq("$knobMinSlider").find(".z-slider-input").val());
		Assertions.assertEquals("true", getEval("(function (inp) { return inp.scrollWidth <= inp.clientWidth; })"
				+ "(jq('$knobMinSlider').find('.z-slider-input')[0])"), "the knob input clips its value");
	}

	@Test
	public void testNoisyMaxposDigits() {
		connect();
		eval("zk.Widget.$(jq('$maxposSlider')).setMaxpos(0.1 * 3).setCurpos(0.1)"); // maxpos 0.30000000000000004
		waitResponse();
		Assertions.assertEquals("0.1", jq("$maxposSlider").find(".z-slider-button").attr("title"));
	}

	@Test
	public void testNoisyStepDigits() {
		connect();
		eval("zk.Widget.$(jq('$defaultStepSlider')).setStep(0.1 * 3).setCurpos(0.6)"); // step 0.30000000000000004
		waitResponse();
		Assertions.assertEquals("0.6", jq("$defaultStepSlider").find(".z-slider-button").attr("title"));
	}

	@Test
	public void testStartDragDigits() {
		connect();
		JQuery slider = jq("$maxposSlider"), btn = slider.find(".z-slider-button");
		double stepInPx = (slider.innerWidth() - btn.outerWidth()) / (1.05 / 0.1);
		// past the 3px drag threshold but short of half a step, so the knob stays at 0
		getActions().clickAndHold(toElement(btn)).moveByOffset((int) Math.round(stepInPx / 3), 0).perform();
		waitResponse();
		Assertions.assertEquals("0.00", jq("#zul_slidetip").text());
		getActions().release().perform();
		waitResponse();
	}

	@Test
	public void testKnobNoisyStepFontSize() {
		connect();
		// 1.05 + 0.1 reads 1.1500000000000001: size the 200px knob's input for "1.15", not for 18 characters
		Assertions.assertEquals("38px", jq("$knobMaxSlider").find(".z-slider-input").css("font-size"));
	}

	private void dragKnob(int fromStep, int toStep, String expected) {
		drag(jq("@slider:eq(0)"), toStep - fromStep, (12 - 10) / 0.05);
		Assertions.assertEquals(expected, jq("$scrollingPos").text());
		Assertions.assertEquals(expected, jq("$scrollPos").text());
	}

	private void drag(JQuery slider, int steps, double totalSteps) {
		hold(slider, steps, totalSteps);
		getActions().release().perform();
		waitResponse();
	}

	private void hold(JQuery slider, int steps, double totalSteps) {
		JQuery btn = slider.find(".z-slider-button");
		double stepInPx = (slider.innerWidth() - btn.outerWidth()) / totalSteps;
		getActions().clickAndHold(toElement(btn))
				.moveByOffset((int) Math.round(steps * stepInPx), 0)
				.perform();
		waitResponse();
	}
}
