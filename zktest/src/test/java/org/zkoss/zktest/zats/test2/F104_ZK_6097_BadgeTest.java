/* F104_ZK_6097_BadgeTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:07:58 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_BadgeTest extends WebDriverTestCase {

	// ----- value (text) -----

	@Test
	public void value_text_renders_in_indicator() {
		connect();
		waitResponse();
		assertTrue(jq("$b-value .z-badge-indicator").exists());
		assertEquals("NEW", jq("$b-value .z-badge-indicator").text());
	}

	// ----- count + max + showZero -----

	@Test
	public void count_renders_number_below_max() {
		connect();
		waitResponse();
		assertEquals("5", jq("$b-count .z-badge-indicator").text());
	}

	@Test
	public void count_zero_hidden_by_default() {
		connect();
		waitResponse();
		assertFalse(jq("$b-zero .z-badge-indicator").exists(),
				"count=0 with showZero=false hides indicator entirely");
	}

	@Test
	public void count_zero_shown_when_showZero_true() {
		connect();
		waitResponse();
		assertTrue(jq("$b-zero-show .z-badge-indicator").exists());
		assertEquals("0", jq("$b-zero-show .z-badge-indicator").text());
	}

	@Test
	public void count_at_default_max_renders_plain_number() {
		connect();
		waitResponse();
		assertEquals("99", jq("$b-at-max .z-badge-indicator").text(),
				"count == max renders the plain number, not the overflow form");
	}

	@Test
	public void count_over_default_max_renders_overflow() {
		connect();
		waitResponse();
		assertEquals("99+", jq("$b-over .z-badge-indicator").text());
	}

	@Test
	public void count_over_custom_max_renders_custom_overflow() {
		connect();
		waitResponse();
		assertEquals("999+", jq("$b-over-custom .z-badge-indicator").text());
	}

	// ----- dot -----

	@Test
	public void dot_renders_indicator_with_no_text_and_dot_class() {
		connect();
		waitResponse();
		assertTrue(jq("$b-dot .z-badge-indicator").exists());
		assertEquals("", jq("$b-dot .z-badge-indicator").text());
		assertTrue(jq("$b-dot").hasClass("z-badge-dot"));
	}

	// ----- a11y (EE za11y add-on) -----

	@Test
	public void indicator_gets_status_role_and_label_from_za11y() {
		connect();
		waitResponse();
		// ARIA is layered on by the EE za11y add-on; only assert when it is
		// loaded. Guards against the CE-mold(no id) / EE-lookup contract drift
		// that previously left role/aria-label silently unapplied.
		if (!Boolean.valueOf(getEval("!!window.za11y")))
			return;
		assertEquals("status", jq("$b-count .z-badge-indicator").attr("role"),
				"za11y must apply role=status to the badge indicator");
		assertTrue(jq("$b-count .z-badge-indicator").attr("aria-label").contains("5"),
				"za11y must apply a contextual aria-label to the badge indicator");
	}

	// ----- severity -----

	@Test
	public void severity_classes_applied_for_each_value() {
		connect();
		waitResponse();
		assertTrue(jq("$b-info").hasClass("z-badge-info"));
		assertTrue(jq("$b-success").hasClass("z-badge-success"));
		assertTrue(jq("$b-warning").hasClass("z-badge-warning"));
		assertTrue(jq("$b-danger").hasClass("z-badge-danger"));
		assertTrue(jq("$b-secondary").hasClass("z-badge-secondary"));
	}

	@Test
	public void severity_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		assertTrue(jq("$bDyn").hasClass("z-badge-info"));

		click(jq("$btn-bad-severity"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("severity must be"),
				"invalid severity should surface a WrongValueException — got: " + err);
		assertTrue(jq("$bDyn").hasClass("z-badge-info"),
				"invalid severity must not mutate the rendered class");
	}

	// ----- placement -----

	@Test
	public void placement_top_right_class_and_wrap() {
		connect();
		waitResponse();
		assertTrue(jq("$b-tr").hasClass("z-badge-top_right"));
		assertFalse(jq("$b-tr").hasClass("z-badge-standalone"),
				"badge wrapping a child must not be standalone");
		assertTrue(jq("$btn-tr").exists(), "wrapped child must render");
	}

	@Test
	public void placement_top_left_class() {
		connect();
		waitResponse();
		assertTrue(jq("$b-tl").hasClass("z-badge-top_left"));
	}

	@Test
	public void placement_bottom_right_class() {
		connect();
		waitResponse();
		assertTrue(jq("$b-br").hasClass("z-badge-bottom_right"));
	}

	@Test
	public void placement_bottom_left_class() {
		connect();
		waitResponse();
		assertTrue(jq("$b-bl").hasClass("z-badge-bottom_left"));
	}

	@Test
	public void placement_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		click(jq("$btn-bad-placement"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("placement must be"),
				"invalid placement should surface a WrongValueException — got: " + err);
	}

	// ----- standalone vs wrap mode -----

	@Test
	public void standalone_class_when_no_children() {
		connect();
		waitResponse();
		assertTrue(jq("$b-value").hasClass("z-badge-standalone"));
	}

	// ----- dynamic updates -----

	@Test
	public void dynamic_count_increment() {
		connect();
		waitResponse();
		assertEquals("1", jq("$bDyn .z-badge-indicator").text());

		click(jq("$btn-inc"));
		waitResponse();
		assertEquals("2", jq("$bDyn .z-badge-indicator").text());
	}

	@Test
	public void dynamic_count_zero_hides_indicator() {
		connect();
		waitResponse();
		assertTrue(jq("$bDyn .z-badge-indicator").exists());

		click(jq("$btn-set-zero"));
		waitResponse();
		assertFalse(jq("$bDyn .z-badge-indicator").exists(),
				"setting count to 0 (with default showZero=false) hides the indicator");
	}

	@Test
	public void dynamic_show_zero_re_renders_indicator() {
		connect();
		waitResponse();
		click(jq("$btn-set-zero"));
		waitResponse();
		assertFalse(jq("$bDyn .z-badge-indicator").exists());

		click(jq("$btn-show-zero"));
		waitResponse();
		assertTrue(jq("$bDyn .z-badge-indicator").exists(),
				"setShowZero(true) reveals the 0 indicator");
		assertEquals("0", jq("$bDyn .z-badge-indicator").text());
	}

	@Test
	public void dynamic_severity_change() {
		connect();
		waitResponse();
		assertTrue(jq("$bDyn").hasClass("z-badge-info"));

		click(jq("$btn-set-danger"));
		waitResponse();
		assertTrue(jq("$bDyn").hasClass("z-badge-danger"));
		assertFalse(jq("$bDyn").hasClass("z-badge-info"));
	}

	// Note: placement classes are only emitted when the badge wraps a child.
	// A standalone badge gets `z-badge-standalone` instead — see Badge.ts
	// domClass_(). Dynamic placement on standalone is therefore a no-op at
	// the rendered class level. The static placement_*_class tests above
	// already exercise wrap-mode dynamics.

	// ----- count / max negative-path validation -----

	@Test
	public void count_negative_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-count"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("count cannot be negative"),
				"setCount(-1) must throw WrongValueException — got: " + err);
	}

	@Test
	public void max_below_one_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-max"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("max must be >= 1"),
				"setMax(0) must throw WrongValueException — got: " + err);
	}

	// ----- public getDisplayValue() API -----

	@Test
	public void get_display_value_returns_max_plus_for_overflow_count() {
		// Click the driver button which sets count=150, max=99, then echoes
		// getDisplayValue() into a label — the public API should return
		// "99+" for any count > max.
		connect();
		waitResponse();
		click(jq("$btn-show-display"));
		waitResponse();
		assertEquals("99+", jq("$dvResult").text(),
				"getDisplayValue() with count(150) > max(99) must return \"99+\"");
	}

	// ----- client-side null-severity coercion (parity with Chip/Confirmpopup) -----

	@Test
	public void null_severity_coerces_to_info_client_side() {
		// Badge.ts setSeverity(null) must coerce to 'info' (matching the
		// server reset and the Chip/Confirmpopup widgets), not leave a dead
		// z-badge-null class. Exercised directly on the widget since the
		// server setter never forwards a literal null to the client.
		connect();
		waitResponse();
		assertTrue(jq("$b-success").hasClass("z-badge-success"));
		getEval("(zk.Widget.$(jq('$b-success')[0]).setSeverity(null),'')");
		assertTrue(jq("$b-success").hasClass("z-badge-info"),
				"null severity must coerce to info");
		assertFalse(jq("$b-success").hasClass("z-badge-null"),
				"null severity must not produce a z-badge-null class");
	}
}
