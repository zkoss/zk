/* F110_ZK_6097_CarouselBindSaveTest.java

	Purpose:

	Description:

	History:
		Sat Aug 30 10:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6097_CarouselBindSaveTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F110-ZK-6097-Carousel-BindSave.zul";
	}

	// activeIndex is client-mutable, so @bind must SAVE as well as LOAD; without
	// the ZKBIND registration the save binding is dropped and the VM never sees it.
	@Test
	public void bind_saves_activeIndex_back_to_viewmodel_on_user_slide() {
		connect();
		waitResponse();
		assertEquals("saved=0", jq("$saved").text(), "initial saved index = 0");
		assertTrue(jq("$cbs0").hasClass("z-carouselitem-active"), "initial slide = 0");

		click(jq("$crBindSave .z-carousel-arrow-next"));
		waitResponse();
		assertTrue(jq("$cbs1").hasClass("z-carouselitem-active"), "client moved to slide 1");
		assertEquals("saved=1", jq("$saved").text(),
				"@bind(vm.activeIndex) must save index 1 back to the ViewModel on onSelect");

		click(jq("$crBindSave .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("saved=2", jq("$saved").text(),
				"each further user slide must save the new index, not just the first");
	}
}
