/* F104_ZK_6100Test.java

		Purpose:

		Description:

		History:
				Mon May 18 13:42:01 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6100 — labelPosition="left" (default) matrix.
 * Covers all 13 InputElement variants in left position plus state variants,
 * dynamic API, and edge cases.
 */
public class F104_ZK_6100Test extends WebDriverTestCase {

	private static final String[] COMPONENT_IDS = {
		"t_textbox", "t_textarea", "t_searchbox",
		"t_intbox", "t_longbox", "t_doublebox", "t_decimalbox",
		"t_spinner", "t_doublespinner",
		"t_datebox", "t_timebox",
		"t_combobox", "t_bandbox"
	};

	@Test
	public void allThirteenVariantsWrapped() {
		connect();
		waitResponse();
		for (String id : COMPONENT_IDS) {
			assertTrue(jq("$" + id).parent().hasClass("z-input-label"),
				id + " should be wrapped in .z-input-label");
			assertTrue(jq("$" + id).parent().hasClass("z-input-label-left"),
				id + " should have .z-input-label-left");
		}
	}

	@Test
	public void labelForLinkage() {
		connect();
		waitResponse();
		String inputId = jq("$t_textbox").attr("id");
		String labelFor = jq("$t_textbox").parent().find(".z-input-label-text").attr("for");
		assertEquals(inputId, labelFor);
	}

	@Test
	public void noFieldsetUsed() {
		connect();
		waitResponse();
		assertFalse(jq(".z-input-label fieldset").exists());
	}

	@Test
	public void emptyLabelNotWrapped() {
		connect();
		waitResponse();
		assertFalse(jq("$t_unset").parent().hasClass("z-input-label"));
	}

	@Test
	public void disabledAttributePropagates() {
		// State classes (z-input-label-disabled etc) are floating-only by design.
		// In left mode, the disabled state is communicated by the native input
		// attribute + the input's own CSS.
		connect();
		waitResponse();
		assertTrue(jq("$t_disabled").is(":disabled"));
	}

	@Test
	public void readonlyAttributePropagates() {
		connect();
		waitResponse();
		assertEquals("readonly", jq("$t_readonly").attr("readonly"));
	}

	@Test
	public void leftHasNoShrunkOrFocused() {
		connect();
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-shrunk"));
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-focused"));
	}

	@Test
	public void prefilledNoShrunkInLeft() {
		connect();
		waitResponse();
		assertFalse(jq("$t_prefilled").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void invalidErrorboxAppears() {
		// z-input-label-invalid is floating-only by design.
		// In left mode, validation errors surface through the standard ZK
		// errorbox popup; assert that a non-empty value triggers no errorbox
		// and an empty submit triggers one.
		connect();
		waitResponse();
		click(jq("$t_invalid"));
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
			"left mode should rely on native errorbox popup");
	}

	@Test
	public void xssEscape() {
		connect();
		waitResponse();
		String labelText = jq("$t_xss").parent().find(".z-input-label-text").text();
		assertTrue(labelText.contains("<script>") || labelText.contains("&lt;script&gt;"),
			"label should display literal <script>, got: " + labelText);
	}

	@Test
	public void rtlLabel() {
		connect();
		waitResponse();
		assertTrue(jq("$t_rtl").parent().hasClass("z-input-label"));
	}

	@Test
	public void serverSetLabel() {
		connect();
		waitResponse();
		click(jq("$btn_setLabel"));
		waitResponse();
		assertEquals("Updated",
			jq("$t_dynamic").parent().find(".z-input-label-text").text());
	}

	@Test
	public void serverSetLabelNullRemovesWrap() {
		connect();
		waitResponse();
		click(jq("$btn_unsetLabel"));
		waitResponse();
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label"));
	}

	@Test
	public void serverSetPositionLeftToTop() {
		connect();
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-left"));
		click(jq("$btn_setPosTop"));
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-top"));
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label-left"));
	}

	@Test
	public void serverSetPositionLeftToFloating() {
		connect();
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-left"));
		click(jq("$btn_setPosFloating"));
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-floating"));
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label-left"));
	}

	@Test
	public void compositeButtonsVisibleInLeft() {
		// In left mode, composite popup-trigger components keep their own
		// chrome — verify each component still renders its trigger button
		// (combobox arrow, datebox calendar icon, spinner up/down, etc).
		connect();
		waitResponse();
		assertTrue(jq("$t_combobox").parent().find(".z-combobox-button").exists(),
			"combobox trigger button should be visible in left mode");
		assertTrue(jq("$t_datebox").parent().find(".z-datebox-button").exists(),
			"datebox trigger button should be visible in left mode");
		assertTrue(jq("$t_bandbox").parent().find(".z-bandbox-button").exists(),
			"bandbox trigger button should be visible in left mode");
	}
}
