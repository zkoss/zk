/* F50_3028270Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 12:41:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertEquals(slider.width() / 2 + 50, btn.positionLeft() + btn.width() / 2);
	}

	private void testHorSlider(JQuery horSlider) {
		getActions().moveToElement(toElement(horSlider))
				.moveByOffset(0, -50)
				.click()
				.perform();
		waitResponse(true);
		JQuery btn = horSlider.find(".z-slider-button");
		Assert.assertEquals(horSlider.height() / 2 - 50, btn.positionTop() + btn.height() / 2);
	}
}
