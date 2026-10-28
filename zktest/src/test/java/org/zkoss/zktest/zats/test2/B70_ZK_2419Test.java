package org.zkoss.zktest.zats.test2;

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
		// ZK-2419: drag 1.5 of the 5.0 range (2.0 to 3.5) along the thumb's track, whose width depends on the theme
		int track = jq("@slider").width() - sliderBtn.outerWidth();
		dragdropTo(sliderBtn, 1, 1, (int) Math.round(track * 1.5 / 5), 1);
		waitResponse();
		assertEquals("3.5", jq(".z-label").last().text());
		assertEquals("3.5", sliderBtn.attr("title"));
	}
}
