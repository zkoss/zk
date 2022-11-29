package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2419Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery sliderBtn = jq(".z-slider-button");
		mouseOver(sliderBtn);
		waitResponse();
		dragdropTo(sliderBtn, 1, 1, 55, 1);
		waitResponse();
		assertEquals("3.5", jq(".z-label").last().text());
		assertEquals("3.5", sliderBtn.attr("title"));
	}
}
