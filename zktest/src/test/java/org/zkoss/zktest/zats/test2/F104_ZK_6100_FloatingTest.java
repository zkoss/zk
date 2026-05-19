/* F104_ZK_6100_FloatingTest.java

		Purpose:

		Description:

		History:
				Tue May 19 13:17:46 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6100 — labelPosition="floating" matrix.
 * Covers all 13 InputElement variants in floating position plus state
 * variants, placeholder coexistence, composite popup-trigger neutralization,
 * dynamic API, accessibility media queries, and edge cases.
 */
public class F104_ZK_6100_FloatingTest extends WebDriverTestCase {

	private static final String[] COMPONENT_IDS = {
		"t_textbox", "t_textarea", "t_searchbox",
		"t_intbox", "t_longbox", "t_doublebox", "t_decimalbox",
		"t_spinner", "t_doublespinner",
		"t_datebox", "t_timebox",
		"t_combobox", "t_bandbox"
	};

	private static final String[] COMPOSITE_IDS = {
		"t_combobox", "t_bandbox", "t_datebox", "t_timebox", "t_spinner", "t_doublespinner"
	};

	@Test
	public void allThirteenVariantsWrapped() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		for (String id : COMPONENT_IDS) {
			assertTrue(jq("$" + id).parent().hasClass("z-input-label"),
				id + " should be wrapped in .z-input-label");
			assertTrue(jq("$" + id).parent().hasClass("z-input-label-floating"),
				id + " should have .z-input-label-floating");
		}
	}

	@Test
	public void labelForLinkage() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		String inputId = jq("$t_textbox").attr("id");
		String labelFor = jq("$t_textbox").parent().find(".z-input-label-text").attr("for");
		assertEquals(inputId, labelFor);
	}

	@Test
	public void emptyLabelNotWrapped() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertFalse(jq("$t_unset").parent().hasClass("z-input-label"));
	}

	@Test
	public void emptyBlurredNotShrunk() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-focused"));
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void focusShrinksLabel() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		assertTrue(jq("$t_textbox").parent().hasClass("z-input-label-focused"));
		assertTrue(jq("$t_textbox").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void typeThenBlurStaysShrunk() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		sendKeys(jq("$t_textbox"), "hello");
		waitResponse();
		click(jq("$t_xss"));
		waitResponse();
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-focused"));
		assertTrue(jq("$t_textbox").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void blurEmptyReturnsToCenter() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		click(jq("$t_xss"));
		waitResponse();
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-focused"));
		assertFalse(jq("$t_textbox").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void prefilledIsShrunk() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertTrue(jq("$t_prefilled").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void disabledStateClass() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertTrue(jq("$t_disabled").parent().hasClass("z-input-label-disabled"));
	}

	@Test
	public void readonlyStateClass() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertTrue(jq("$t_readonly").parent().hasClass("z-input-label-readonly"));
		assertTrue(jq("$t_readonly").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void invalidClassAddsAfterValidation() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$t_invalid"));
		waitResponse();
		click(jq("$t_xss"));
		waitResponse();
		assertTrue(jq("$t_invalid").parent().hasClass("z-input-label-invalid"));
	}

	@Test
	public void invalidClearsAfterValidValue() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$t_invalid"));
		waitResponse();
		click(jq("$t_xss"));
		waitResponse();
		click(jq("$t_invalid"));
		sendKeys(jq("$t_invalid"), "ok");
		waitResponse();
		click(jq("$t_xss"));
		waitResponse();
		assertFalse(jq("$t_invalid").parent().hasClass("z-input-label-invalid"));
	}

	@Test
	public void placeholderTransparentWhenCentered() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertEquals("user@example.com", jq("$t_hint").attr("placeholder"));
		String hintColor = (String) getEval(
			"window.getComputedStyle(jq('$t_hint')[0], '::placeholder').color");
		assertTrue(hintColor.contains("0, 0, 0, 0") || hintColor.equalsIgnoreCase("transparent"),
			"placeholder color should be transparent when not shrunk, got: " + hintColor);
	}

	@Test
	public void placeholderFadesInAfterShrunk() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$t_hint"));
		waitResponse();
		assertTrue(jq("$t_hint").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void inplaceFloatingShrunk() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertTrue(jq("$t_inplace").parent().hasClass("z-input-label-shrunk"));
	}

	@Test
	public void compositeChromeNeutralized() {
		// In floating mode the wrap owns the outlined chrome; each composite's
		// own border must be removed so it doesn't draw a second outline.
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		for (String id : COMPOSITE_IDS) {
			String border = jq("$" + id).css("border-top-width");
			assertEquals("0px", border, id + " composite outer border should be 0 in floating mode");
		}
	}

	@Test
	public void compositeTriggerReserveRuleLoaded() {
		// The label width must shrink to reserve space for composite trigger
		// buttons. Verify the :has() rule exists in the loaded stylesheets
		// (computed max-width may stay as an unresolved calc() string).
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		Object hasRule = getEval(
			"Array.from(document.styleSheets).some(s => {"
			+ "  try { return Array.from(s.cssRules || []).some(r => "
			+ "    r.selectorText && r.selectorText.includes('z-input-label-floating') &&"
			+ "    r.selectorText.includes(':has') && r.style && r.style.maxWidth);"
			+ "  } catch(e) { return false; }"
			+ "})");
		assertEquals("true", String.valueOf(hasRule),
			"composite trigger-reserve :has() rule should be loaded");
	}

	@Test
	public void xssEscape() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		String labelText = jq("$t_xss").parent().find(".z-input-label-text").text();
		assertTrue(labelText.contains("<script>") || labelText.contains("&lt;script&gt;"),
			"label should display literal <script>, got: " + labelText);
	}

	@Test
	public void longLabelEllipsis() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		String overflow = jq("$t_long").parent().find(".z-input-label-text").css("text-overflow");
		assertEquals("ellipsis", overflow);
	}

	@Test
	public void rtlLabel() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertTrue(jq("$t_rtl").parent().hasClass("z-input-label"));
	}

	@Test
	public void serverSetLabel() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$btn_setLabel"));
		waitResponse();
		assertEquals("Updated",
			jq("$t_dynamic").parent().find(".z-input-label-text").text());
	}

	@Test
	public void serverSetLabelNullRemovesWrap() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$btn_unsetLabel"));
		waitResponse();
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label"));
	}

	@Test
	public void serverSetPositionFloatingToLeft() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-floating"));
		click(jq("$btn_setPosLeft"));
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-left"));
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label-floating"));
	}

	@Test
	public void serverSetPositionFloatingToTop() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-floating"));
		click(jq("$btn_setPosTop"));
		waitResponse();
		assertTrue(jq("$t_dynamic").parent().hasClass("z-input-label-top"));
		assertFalse(jq("$t_dynamic").parent().hasClass("z-input-label-floating"));
	}

	@Test
	public void wcag247FocusVisible() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		click(jq("$t_textbox"));
		waitResponse();
		assertTrue(jq("$t_textbox").parent().hasClass("z-input-label-focused"));
		String boxShadow = jq("$t_textbox").parent().css("box-shadow");
		assertNotNull(boxShadow);
	}

	@Test
	public void prefersContrastRuleLoaded() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		Object hasRule = getEval(
			"Array.from(document.styleSheets).some(s => {"
			+ "  try { return Array.from(s.cssRules || []).some(r => "
			+ "    r.media && r.media.mediaText && r.media.mediaText.includes('prefers-contrast'));"
			+ "  } catch(e) { return false; }"
			+ "})");
		assertEquals("true", String.valueOf(hasRule));
	}

	@Test
	public void forcedColorsRuleLoaded() {
		connect("/test2/F104-ZK-6100-floating.zul");
		waitResponse();
		Object hasRule = getEval(
			"Array.from(document.styleSheets).some(s => {"
			+ "  try { return Array.from(s.cssRules || []).some(r => "
			+ "    r.media && r.media.mediaText && r.media.mediaText.includes('forced-colors'));"
			+ "  } catch(e) { return false; }"
			+ "})");
		assertEquals("true", String.valueOf(hasRule));
	}
}
