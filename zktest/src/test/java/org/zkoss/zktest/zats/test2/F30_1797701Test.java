/* F30_1797701Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 12:24:05 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F30_1797701Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		waitResponse();
		int sliderWidth = jq("@slider").width();
		act.clickAndHold(toElement(jq(".z-slider-button"))).moveByOffset(sliderWidth, 0).perform();
		waitResponse();
		Assertions.assertEquals("Here is a position : 100", jq(".z-slider-popup").text().trim());
		act.release().perform();
		waitResponse();
		click(jq("@button:contains(change)"));
		waitResponse();
		act.clickAndHold(toElement(jq(".z-slider-button"))).moveByOffset(-sliderWidth / 2, 0).perform();
		waitResponse();
		MatcherAssert.assertThat(jq(".z-slider-popup").text().trim(), Matchers.matchesRegex("position \\d+ is here"));
	}
}
