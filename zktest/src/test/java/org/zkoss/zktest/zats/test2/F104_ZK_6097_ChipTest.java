/* F104_ZK_6097_ChipTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:08:24 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_ChipTest extends WebDriverTestCase {

	// ----- severity -----

	@Test
	public void severity_default_is_info() {
		connect();
		waitResponse();
		// Default severity = "info" but no explicit class attached when default
		// (Chip.renderProperties skips rendering when severity == "info").
		// We assert no other severity class is on the element.
		assertFalse(jq("$chip-default").hasClass("z-chip-success"));
		assertFalse(jq("$chip-default").hasClass("z-chip-danger"));
	}

	@Test
	public void severity_info_class_applied_when_explicit() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-info").hasClass("z-chip-info"));
	}

	@Test
	public void severity_success_class() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-success").hasClass("z-chip-success"));
	}

	@Test
	public void severity_warning_class() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-warning").hasClass("z-chip-warning"));
	}

	@Test
	public void severity_danger_class() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-danger").hasClass("z-chip-danger"));
	}

	@Test
	public void severity_secondary_class() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-secondary").hasClass("z-chip-secondary"));
	}

	@Test
	public void severity_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		assertTrue(jq("$chipDyn").hasClass("z-chip-info"));

		click(jq("$btn-bad-severity"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("severity must be"),
				"invalid severity should surface a WrongValueException — got: " + err);
		assertTrue(jq("$chipDyn").hasClass("z-chip-info"),
				"invalid severity must not mutate the rendered class");
	}

	@Test
	public void severity_dynamic_change() {
		connect();
		waitResponse();
		assertTrue(jq("$chipDyn").hasClass("z-chip-info"));

		click(jq("$btn-set-success"));
		waitResponse();
		assertTrue(jq("$chipDyn").hasClass("z-chip-success"));
		assertFalse(jq("$chipDyn").hasClass("z-chip-info"));
	}

	// ----- label -----

	@Test
	public void label_renders_in_dom() {
		connect();
		waitResponse();
		assertEquals("INFO", jq("$chip-info").text());
	}

	// ----- rounded -----

	@Test
	public void rounded_default_false_no_class() {
		connect();
		waitResponse();
		assertFalse(jq("$chip-info").hasClass("z-chip-rounded"));
	}

	@Test
	public void rounded_true_class_applied() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-rounded").hasClass("z-chip-rounded"));
	}

	@Test
	public void rounded_dynamic_change() {
		connect();
		waitResponse();
		assertFalse(jq("$chipDyn").hasClass("z-chip-rounded"));
		click(jq("$btn-set-rounded"));
		waitResponse();
		assertTrue(jq("$chipDyn").hasClass("z-chip-rounded"));
	}

	// ----- closable + onClose -----

	@Test
	public void closable_default_no_close_button() {
		connect();
		waitResponse();
		assertFalse(jq("$chip-info .z-chip-close").exists());
	}

	@Test
	public void closable_true_renders_close_button() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-closable").hasClass("z-chip-closable"));
		assertTrue(jq("$chip-closable .z-chip-close").exists());
	}

	@Test
	public void close_button_gets_aria_label_from_za11y() {
		connect();
		waitResponse();
		// ARIA is layered on by the EE za11y add-on; only assert when loaded.
		// Smoke test for the CE-mold / EE-augment contract on the close button,
		// guarding against silent a11y drift (cf. Badge HR-1).
		if (!Boolean.valueOf(getEval("!!window.za11y")))
			return;
		String label = jq("$chip-closable .z-chip-close").attr("aria-label");
		assertTrue(label != null && label.contains("Remove"),
				"za11y must apply a 'Remove <label>' aria-label to the chip close button — got: " + label);
	}

	@Test
	public void closable_onClose_event_fires_on_server() {
		connect();
		waitResponse();
		assertEquals("no-close", jq("$closeResult").text());
		click(jq("$chip-close-event .z-chip-close"));
		waitResponse();
		assertEquals("closed", jq("$closeResult").text());
	}

	@Test
	public void closable_onClose_stopPropagation_keeps_chip_visible() {
		connect();
		waitResponse();
		click(jq("$chip-close-stopped .z-chip-close"));
		waitResponse();
		assertEquals("stopped", jq("$closeResult").text());
		assertTrue(jq("$chip-close-stopped").exists(),
				"chip must still be attached after stopPropagation()");
		assertNotEquals("none", jq("$chip-close-stopped").css("display"),
				"chip must NOT be display:none after stopPropagation() — "
				+ "the client must not optimistically hide before the AU "
				+ "response confirms detach");
	}

	@Test
	public void closable_dynamic_toggle_on() {
		connect();
		waitResponse();
		assertFalse(jq("$chipDyn .z-chip-close").exists());
		click(jq("$btn-set-closable"));
		waitResponse();
		assertTrue(jq("$chipDyn .z-chip-close").exists());
	}

	// ----- size -----

	@Test
	public void size_default_medium_no_class() {
		connect();
		waitResponse();
		assertFalse(jq("$chip-info").hasClass("z-chip-small"),
				"default size 'medium' must not emit a size class");
	}

	@Test
	public void size_small_class_applied() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-small").hasClass("z-chip-small"));
	}

	@Test
	public void size_medium_explicit_no_class() {
		connect();
		waitResponse();
		assertFalse(jq("$chip-medium").hasClass("z-chip-small"));
	}

	@Test
	public void size_dynamic_change() {
		connect();
		waitResponse();
		click(jq("$btn-set-small"));
		waitResponse();
		assertTrue(jq("$chipDyn").hasClass("z-chip-small"));
	}

	@Test
	public void size_invalid_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-size"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("size must be small or medium"),
				"invalid size should throw — got: " + err);
	}

	// ----- disabled -----

	@Test
	public void disabled_default_false_no_class() {
		connect();
		waitResponse();
		assertFalse(jq("$chip-info").hasClass("z-chip-disabled"));
	}

	@Test
	public void disabled_true_class_applied() {
		connect();
		waitResponse();
		assertTrue(jq("$chip-disabled").hasClass("z-chip-disabled"));
	}

	@Test
	public void disabled_dynamic_change() {
		connect();
		waitResponse();
		click(jq("$btn-set-disabled"));
		waitResponse();
		assertTrue(jq("$chipDyn").hasClass("z-chip-disabled"));
	}

	// ----- custom color via CSS variables -----

	@Test
	public void cssvar_hex_applied_via_style_attribute() {
		// Custom chip colors flow through the --zk-chip-bg / --zk-chip-color
		// CSS variables declared in chip.less; the chip itself has no `color`
		// attribute so users pipe values through the standard `style=` path.
		// The ZUL sets --zk-chip-bg:#6366f1 → rgb(99, 102, 241).
		connect();
		waitResponse();
		String style = jq("$chip-cssvar-hex").attr("style");
		assertNotNull(style, "chip with CSS-var color must carry the inline style attribute");
		assertTrue(style.contains("--zk-chip-bg"),
				"CSS-var color must set --zk-chip-bg — got: " + style);
		// Resolved computed background must equal the override (#6366f1
		// → rgb(99, 102, 241)), NOT the severity-default info background
		// (rgb(230, 244, 255)). Strip spaces and the optional rgba alpha so
		// browsers that emit rgb(...) or rgba(..., 1) both pass.
		String bg = jq("$chip-cssvar-hex").css("background-color");
		assertNotNull(bg);
		String normalized = bg.replace(" ", "");
		assertTrue(normalized.startsWith("rgb(99,102,241)")
						|| normalized.startsWith("rgba(99,102,241"),
				"computed background-color should resolve --zk-chip-bg=#6366f1 "
						+ "to rgb(99, 102, 241) — got: " + bg);
	}

	@Test
	public void cssvar_named_applied() {
		connect();
		waitResponse();
		String style = jq("$chip-cssvar-named").attr("style");
		assertNotNull(style);
		assertTrue(style.toLowerCase().contains("--zk-chip-bg:tomato"),
				"named-color CSS var must land verbatim in style — got: " + style);
	}

	@Test
	public void cssvar_dynamic_set_then_clear() {
		connect();
		waitResponse();
		String before = jq("$chipDyn").attr("style");
		assertTrue(before == null || !before.contains("--zk-chip-bg"));

		click(jq("$btn-set-cssvar"));
		waitResponse();
		String after = jq("$chipDyn").attr("style");
		assertNotNull(after);
		assertTrue(after.contains("--zk-chip-bg"),
				"setStyle with --zk-chip-bg should propagate to the inline style — got: " + after);

		click(jq("$btn-clear-cssvar"));
		waitResponse();
		String cleared = jq("$chipDyn").attr("style");
		assertTrue(cleared == null || !cleared.contains("--zk-chip-bg"),
				"setStyle(null) should drop the CSS-var override — got: " + cleared);
	}

	// ----- close affordance is a real <button> -----

	@Test
	public void close_button_is_real_button_element() {
		// Real <button> gives native keyboard activation + correct disabled
		// propagation. Replacing the prior <i role="button" tabindex="0">.
		connect();
		waitResponse();
		assertTrue(jq("$chip-closable").find("button.z-chip-close").exists(),
				"closable chip's remove affordance must be a <button> "
						+ "(not <i role=button>)");
	}

	@Test
	public void close_button_disabled_when_chip_disabled() {
		// Chip with disabled=true must propagate to the inner close button
		// so AT and form-submission machinery recognize it as inactive.
		connect();
		waitResponse();
		String disabled = jq("$chip-disabled").find("button.z-chip-close").attr("disabled");
		assertTrue(disabled != null && (disabled.equals("disabled") || disabled.equals("true") || disabled.equals("")),
				"disabled chip's close button must carry the disabled attribute — got: " + disabled);
	}

	@Test
	public void disabled_chip_onClose_does_not_detach_on_crafted_request() {
		// The close button renders disabled, so a normal click can't reach
		// onClose — but a hand-crafted onClose AU request can. The server-side
		// guard (Chip#onClose -> if isDisabled() return) must refuse to detach,
		// i.e. the server must not trust the client's disabled enforcement.
		connect();
		waitResponse();
		assertTrue(jq("$chip-disabled").exists());
		// Fire onClose straight from the widget, bypassing the disabled button.
		getEval("(zk.Widget.$(jq('$chip-disabled')[0]).fire('onClose',{},{toServer:true}),'')");
		waitResponse();
		assertTrue(jq("$chip-disabled").exists(),
				"a disabled chip must NOT detach on a crafted onClose");

		// Control: an enabled closable chip detaches via the same path,
		// proving the test exercises the onClose -> detach route.
		assertTrue(jq("$chip-closable").exists());
		getEval("(zk.Widget.$(jq('$chip-closable')[0]).fire('onClose',{},{toServer:true}),'')");
		waitResponse();
		assertFalse(jq("$chip-closable").exists(),
				"an enabled closable chip detaches on onClose (default handler)");
	}

	@Test
	public void close_button_aria_label_refreshes_after_setLabel() {
		// 6-10: the close button announces "Remove <label>"; setLabel updates
		// the chip text in place (no rerender), so the za11y augment must
		// refresh the close-button aria-label or it keeps the old name.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		getEval("(zk.Widget.$(jq('$chip-closable')[0]).setLabel('Bob'),'')");
		waitResponse();
		String aria = getEval(
				"zk.Widget.$(jq('$chip-closable')[0]).$n('close').getAttribute('aria-label')");
		assertTrue(aria != null && aria.contains("Bob"),
				"close-button aria-label must follow the new label after setLabel, was: " + aria);
	}
}
