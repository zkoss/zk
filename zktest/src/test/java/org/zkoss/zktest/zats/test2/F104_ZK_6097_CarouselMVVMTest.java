/* F104_ZK_6097_CarouselMVVMTest.java

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

public class F104_ZK_6097_CarouselMVVMTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Carousel-MVVM.zul";
	}

	// activeIndex @bind: clicking next fires onSelect @command, VM updates, @load pushes result
	@Test
	public void mvvm_onSelect_command_updates_result_and_activeIndex() {
		connect();
		waitResponse();
		assertEquals("none", jq("$mvvmSelResult").text(), "initial selResult = none");
		assertTrue(jq("$cmv0").hasClass("z-carouselitem-active"), "initial slide = 0");

		click(jq("$crMvvm .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("i=1", jq("$mvvmSelResult").text(),
				"@command onSlideSelected must update selResult via @NotifyChange");
		assertTrue(jq("$cmv1").hasClass("z-carouselitem-active"),
				"@bind(vm.activeIndex) must reflect index=1");
	}

	// onChanging @command fires before onSelect and carries from/to
	@Test
	public void mvvm_onChanging_command_fires_with_from_and_to() {
		connect();
		waitResponse();
		click(jq("$crMvvm .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("from=0 to=1", jq("$mvvmChangingResult").text(),
				"@command onSlideChanging must carry fromIndex and toIndex");
	}

	// @bind activeIndex: VM @command goToSecond changes activeIndex -> carousel jumps to slide 2
	@Test
	public void mvvm_server_driven_activeIndex_bind() {
		connect();
		waitResponse();
		assertTrue(jq("$cmv0").hasClass("z-carouselitem-active"), "start at slide 0");

		click(jq("$btnGoToSecond"));
		waitResponse();
		assertTrue(jq("$cmv1").hasClass("z-carouselitem-active"),
				"@command goToSecond sets activeIndex=1 on VM; @bind pushes it to carousel");
		assertFalse(jq("$cmv0").hasClass("z-carouselitem-active"),
				"slide 0 must no longer be active");
	}

	// Round-trip: server changes activeIndex, client reflects, then user clicks, server notified
	@Test
	public void mvvm_bidirectional_activeIndex_bind_round_trip() {
		connect();
		waitResponse();
		click(jq("$btnGoToSecond"));
		waitResponse();
		assertTrue(jq("$cmv1").hasClass("z-carouselitem-active"));

		// Now click Next on client — VM should receive new index via @command
		click(jq("$crMvvm .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("i=2", jq("$mvvmSelResult").text(),
				"after server set index=1, user click-next must fire @command with idx=2");
	}
}
