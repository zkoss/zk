/* F50_3028270Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 12:41:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3028270Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testSlider(jq("@slider:eq(0)"));
		testSlider(jq("@slider:eq(1)"));
		testSlider(jq("@slider:eq(2)"));
		testHorSlider(jq("@slider:eq(3)"));
	}

	private void testSlider(JQuery slider) {
		getActions().moveToElement(toElement(slider))
				.moveByOffset(50, 0)
				.click()
				.perform();
		waitResponse(true);
		JQuery btn = slider.find(".z-slider-button");
		double left = parseDouble(slider + ".offset().left");
		double cursor = parseDouble("zk.currentPointer[0]") - left;
		double unit = slider.width() / 100.0;
		// 3028270: the thumb stops on the slider's 1/100 grid, so it may sit up to one step short of the cursor
		Assertions.assertEquals(cursor - unit / 2, parseDouble(btn + ".offset().left") - left + btn.width() / 2.0, unit / 2 + 0.5);
	}

	private void testHorSlider(JQuery horSlider) {
		getActions().moveToElement(toElement(horSlider))
				.moveByOffset(0, -50)
				.click()
				.perform();
		waitResponse(true);
		JQuery btn = horSlider.find(".z-slider-button");
		Assertions.assertEquals(horSlider.height() / 2 - 50, btn.offsetTop() - horSlider.offsetTop() + btn.height() / 2, 2);
	}

	private double parseDouble(String script) {
		return Double.parseDouble(getEval(script));
	}
}
