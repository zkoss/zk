/* F111_ZK_5710Test.java

		Purpose:

		Description:

		History:
				Wed May 13 23:54:47 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F111_ZK_5710Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();
		assertTrue(jq("$tb5").find(".z-toolbar-overflowpopup-button").exists());

		assertFalse(jq("$tb6").hasClass("z-toolbar-icon-only"));
		assertFalse(jq("$tb6Save").find(".z-toolbarbutton-label").exists());
		assertTrue(jq("$tb3").hasClass("z-toolbar-icon-only"));
		assertFalse(jq("$tb4").hasClass("z-toolbar-icon-only"));

		assertFalse(jq("$tb1").hasClass("z-toolbar-icon-only"));
		int tb1Height = jq("$tb1").outerHeight(),
			saveWidth = jq("$tb1Save").outerWidth(),
			saveLabelWidth = jq("$tb1Save").find(".z-toolbarbutton-label").outerWidth(),
			deleteWidth = jq("$tb1Delete").outerWidth(),
			deleteLabelWidth = jq("$tb1Delete").find(".z-button-label").outerWidth(),
			textWidth = jq("$tb1Text").outerWidth(),
			tb2SaveWidth = jq("$tb2Save").outerWidth();

		click(jq("$shrink"));
		waitResponse();
		assertTrue(jq("$tb1").hasClass("z-toolbar-icon-only"));
		assertEquals(tb1Height, jq("$tb1").outerHeight());
		assertTrue(jq("$tb1Save").outerWidth() < saveWidth - saveLabelWidth / 2);
		assertTrue(jq("$tb1Save").text().contains("Save"));
		assertTrue(jq("$tb1Delete").outerWidth() < deleteWidth - deleteLabelWidth / 2);
		assertTrue(jq("$tb1Delete").text().contains("Delete"));
		assertEquals(textWidth, jq("$tb1Text").outerWidth());
		assertFalse(jq("$tb2").hasClass("z-toolbar-icon-only"));
		assertEquals(tb2SaveWidth, jq("$tb2Save").outerWidth());
		int iconOnlySaveWidth = jq("$tb1Save").outerWidth();
		click(jq("$tb1Sclass"));
		waitResponse();
		assertTrue(jq("$tb1").hasClass("z-toolbar-icon-only"));
		assertEquals(iconOnlySaveWidth, jq("$tb1Save").outerWidth());

		click(jq("$expand"));
		waitResponse();
		assertFalse(jq("$tb1").hasClass("z-toolbar-icon-only"));
		assertEquals(saveWidth, jq("$tb1Save").outerWidth());
		assertEquals(deleteWidth, jq("$tb1Delete").outerWidth());

		click(jq("$tb3None"));
		waitResponse();
		assertFalse(jq("$tb3").hasClass("z-toolbar-icon-only"));
		assertFalse(jq("$tb3Save").find(".z-toolbarbutton-label").exists());
		click(jq("$tb3Clear"));
		waitResponse();
		assertTrue(jq("$tb3").hasClass("z-toolbar-icon-only"));
		assertTrue(jq("$tb3Save").find(".z-toolbarbutton-label").exists());
	}

	@Test
	public void testPanelMold() {
		connect();
		assertTrue(jq("$tb7").hasClass("z-toolbar-icon-only"));
		assertTrue(jq("$tb7Save").find(".z-toolbarbutton-label").outerWidth() <= 1);
		assertTrue(jq("$tb7Save").text().contains("Save"));
		assertTrue(jq("$tb7Delete").find(".z-button-label").outerWidth() <= 1);
	}

	@Test
	public void testOverflowPopup() {
		connect();
		assertTrue(jq("$tb8").hasClass("z-toolbar-icon-only"));
		assertFalse(jq("$tb8").find(".z-toolbar-popup .z-toolbarbutton").exists());
		assertTrue(jq("$tb8").hasClass("z-toolbar-overflowpopup-off"));
	}

	@Test
	public void testHidden() {
		connect();
		assertFalse(jq("$tb9").hasClass("z-toolbar-icon-only"));
		click(jq("$tb9Toggle"));
		waitResponse();
		sleep(300); // let the ResizeObserver callback run
		assertFalse(jq("$tb9").hasClass("z-toolbar-icon-only"));
		click(jq("$tb9Toggle"));
		waitResponse();
		sleep(300);
		assertFalse(jq("$tb9").hasClass("z-toolbar-icon-only"));
	}
}
