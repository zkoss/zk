/* F104_ZK_6097_CarouselMVCTest.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_CarouselMVCTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Carousel-MVC.zul";
	}

	// @Listen onSelect routes event data to Composer
	@Test
	public void mvc_onSelect_routed_to_composer() {
		connect();
		waitResponse();
		assertEquals("none", jq("$mvcSelResult").text());

		click(jq("$crMvc .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("i=1", jq("$mvcSelResult").text(),
				"@Listen(onSelect=#crMvc) must receive index from event data");
	}

	// @Listen onChanging routes event data (from/to) to Composer
	@Test
	public void mvc_onChanging_routed_to_composer() {
		connect();
		waitResponse();
		click(jq("$crMvc .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("from=0 to=1", jq("$mvcChangingResult").text(),
				"@Listen(onChanging=#crMvc) must receive fromIndex and toIndex");
	}

	// Composer programmatic next: setActiveIndex(+1) advances slide
	@Test
	public void mvc_programmatic_next_advances_slide() {
		connect();
		waitResponse();
		assertTrue(jq("$cmc0").hasClass("z-carouselitem-active"), "start at slide 0");

		click(jq("$btnNextMvc"));
		waitResponse();
		assertTrue(jq("$cmc1").hasClass("z-carouselitem-active"),
				"Composer setActiveIndex(1) must activate slide 1");
		assertFalse(jq("$cmc0").hasClass("z-carouselitem-active"));
	}

	// Composer prev stays at 0 when already at first
	@Test
	public void mvc_programmatic_prev_from_first_stays_at_first() {
		connect();
		waitResponse();
		assertTrue(jq("$cmc0").hasClass("z-carouselitem-active"));

		click(jq("$btnPrevMvc"));
		waitResponse();
		assertTrue(jq("$cmc0").hasClass("z-carouselitem-active"),
				"Composer prev with idx=0 must not navigate below 0");
	}
}
