/* F104_ZK_6100_TopTest.java

		Purpose:

		Description:

		History:
				Tue May 19 13:17:46 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6100 — labelPosition="top" matrix.
 * Covers all 13 InputElement variants in top position plus state variants,
 * dynamic API, and edge cases. Top position uses column-flex layout so the
 * label stacks above the editor; it shares the static state model with
 * left position (no shrunk/focused transitions).
 */
public class F104_ZK_6100_TopTest extends WebDriverTestCase {

	private static final String[] COMPONENT_IDS = {
		"t_textbox", "t_textarea", "t_searchbox",
		"t_intbox", "t_longbox", "t_doublebox", "t_decimalbox",
		"t_spinner", "t_doublespinner",
		"t_datebox", "t_timebox",
		"t_combobox", "t_bandbox"
	};

	@Test
	public void allThirteenVariantsWrapped() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		for (String id : COMPONENT_IDS) {
			assertTrue(jq("$" + id).parent().hasClass("z-input-label"),
				id + " should be wrapped in .z-input-label");
			assertTrue(jq("$" + id).parent().hasClass("z-input-label-top"),
				id + " should have .z-input-label-top");
		}
	}

	@Test
	public void labelStackedAboveInput() {
		// Column-flex layout — label and input stack vertically.
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		String flexDirection = jq("$t_textbox").parent().css("flex-direction");
		assertEquals("column", flexDirection);
	}

	@Test
	public void labelForLinkage() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		String inputId = jq("$t_textbox").attr("id");
		String labelFor = jq("$t_textbox").parent().find(".z-input-label-text").attr("for");
		assertEquals(inputId, labelFor);
	}

	@Test
	public void emptyLabelNotWrapped() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		assertFalse(jq("$t_unset").parent().hasClass("z-input-label"));
	}

	@Test
	public void disabledAttributePropagates() {
		// State classes are floating-only by design; in top mode the disabled
		// state surfaces via the native input attribute.
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		assertTrue(jq("$t_disabled").is(":disabled"));
	}

	@Test
	public void readonlyAttributePropagates() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		assertEquals("readonly", jq("$t_readonly").attr("readonly"));
	}

	@Test
	public void topHasNoShrunkOrFocused() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-shrunk"));
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-focused"));
	}

	@Test
	public void invalidErrorboxAppears() {
		// z-input-label-invalid is floating-only by design — top mode falls
		// back to the standard ZK errorbox popup for validation errors.
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		click(jq("$t_invalid"));
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
			"top mode should rely on native errorbox popup");
	}

	@Test
	public void xssEscape() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		String labelText = jq("$t_xss").parent().find(".z-input-label-text").text();
		assertTrue(labelText.contains("<script>") || labelText.contains("&lt;script&gt;"),
			"label should display literal <script>, got: " + labelText);
	}

	@Test
	public void rtlLabel() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		assertTrue(jq("$t_rtl").parent().hasClass("z-input-label"));
	}

	@Test
	public void serverSetLabel() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		click(jq("$btn_setLabel"));
		waitResponse();
		assertEquals("Updated",
			jq("$t_dynamic").parent().find(".z-input-label-text").text());
	}

	@Test
	public void serverSetLabelNullRemovesWrap() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		click(jq("$btn_unsetLabel"));
		waitResponse();
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label"));
	}

	@Test
	public void serverSetPositionTopToLeft() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-top"));
		click(jq("$btn_setPosLeft"));
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-left"));
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label-top"));
	}

	@Test
	public void serverSetPositionTopToFloating() {
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-top"));
		click(jq("$btn_setPosFloating"));
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-floating"));
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label-top"));
	}

	@Test
	public void compositeButtonsVisibleInTop() {
		// In top mode, composite popup-trigger components keep their own
		// chrome — verify each component still renders its trigger button.
		connect("/test2/F104-ZK-6100-top.zul");
		waitResponse();
		assertTrue(jq("$t_combobox").parent().find(".z-combobox-button").exists(),
			"combobox trigger button should be visible in top mode");
		assertTrue(jq("$t_datebox").parent().find(".z-datebox-button").exists(),
			"datebox trigger button should be visible in top mode");
		assertTrue(jq("$t_bandbox").parent().find(".z-bandbox-button").exists(),
			"bandbox trigger button should be visible in top mode");
	}
}
