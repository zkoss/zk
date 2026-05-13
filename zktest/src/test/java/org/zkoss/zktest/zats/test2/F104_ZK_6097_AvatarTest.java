/* F104_ZK_6097_AvatarTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:08:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_AvatarTest extends WebDriverTestCase {

	// ----- Render fallback chain: image > label > icon -----

	@Test
	public void render_image_renders_img() {
		connect();
		waitResponse();
		assertTrue(jq("$av-img img").exists(), "image avatar must render <img>");
	}

	@Test
	public void render_label_renders_text() {
		connect();
		waitResponse();
		assertTrue(jq("$av-label .z-avatar-text").exists(),
				"label-only avatar renders the text span");
		assertEquals("AL", jq("$av-label .z-avatar-text").text());
		assertFalse(jq("$av-label img").exists(),
				"label-only avatar must not render <img>");
	}

	@Test
	public void render_icon_renders_i() {
		connect();
		waitResponse();
		assertTrue(jq("$av-icon i").exists(), "icon-only avatar renders <i>");
		assertFalse(jq("$av-icon img").exists());
		assertFalse(jq("$av-icon .z-avatar-text").exists());
	}

	@Test
	public void render_image_takes_precedence_over_label() {
		connect();
		waitResponse();
		assertTrue(jq("$av-img-label img").exists(),
				"image+label avatar renders <img>");
		assertFalse(jq("$av-img-label .z-avatar-text").exists(),
				"image present, text fallback must be suppressed");
	}

	@Test
	public void render_icon_takes_precedence_over_label() {
		// LabelImageElement priority: image > icon > label. With both label
		// and iconSclass set, the avatar must render <i>, not the label text.
		connect();
		waitResponse();
		assertTrue(jq("$av-label-icon i").exists(),
				"label+icon avatar renders <i>, not the text fallback");
		assertFalse(jq("$av-label-icon .z-avatar-text").exists(),
				"icon present, text fallback must be suppressed");
	}

	// ----- shape -----

	@Test
	public void shape_default_is_circle() {
		connect();
		waitResponse();
		assertTrue(jq("$av-default").hasClass("z-avatar-circle"),
				"avatar with no shape attribute defaults to circle");
		assertFalse(jq("$av-default").hasClass("z-avatar-square"));
	}

	@Test
	public void shape_explicit_circle() {
		connect();
		waitResponse();
		assertTrue(jq("$av-circle").hasClass("z-avatar-circle"));
	}

	@Test
	public void shape_square() {
		connect();
		waitResponse();
		assertTrue(jq("$av-square").hasClass("z-avatar-square"));
		assertFalse(jq("$av-square").hasClass("z-avatar-circle"));
	}

	@Test
	public void shape_dynamic_change() {
		connect();
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-circle"));

		click(jq("$btn-set-shape"));
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-square"),
				"setShape('square') updates the class");
		assertFalse(jq("$avDyn").hasClass("z-avatar-circle"));

		click(jq("$btn-reset-shape"));
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-circle"),
				"setShape('circle') restores the class");
	}

	@Test
	public void shape_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-circle"));

		click(jq("$btn-shape-invalid"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("shape must be circle or square"),
				"invalid shape should surface a WrongValueException — got: " + err);
		assertTrue(jq("$avDyn").hasClass("z-avatar-circle"),
				"invalid shape must not mutate the rendered class");
	}

	// ----- size -----

	@Test
	public void size_default_is_medium() {
		connect();
		waitResponse();
		assertTrue(jq("$av-default").hasClass("z-avatar-medium"));
	}

	@Test
	public void size_small() {
		connect();
		waitResponse();
		assertTrue(jq("$av-small").hasClass("z-avatar-small"));
	}

	@Test
	public void size_large() {
		connect();
		waitResponse();
		assertTrue(jq("$av-large").hasClass("z-avatar-large"));
	}

	@Test
	public void size_dynamic_change() {
		connect();
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-medium"));

		click(jq("$btn-set-size"));
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-large"));
		assertFalse(jq("$avDyn").hasClass("z-avatar-medium"));

		click(jq("$btn-reset-size"));
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-small"));
	}

	@Test
	public void size_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		assertTrue(jq("$avDyn").hasClass("z-avatar-medium"));

		click(jq("$btn-size-invalid"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("size must be small, medium or large"),
				"invalid size should surface a WrongValueException — got: " + err);
		assertTrue(jq("$avDyn").hasClass("z-avatar-medium"),
				"invalid size must not mutate the rendered class");
	}

	// ----- gap (Ant Design Avatar) -----

	@Test
	public void gap_default_no_inline_padding() {
		connect();
		waitResponse();
		String style = jq("$av-gap-default .z-avatar-text").attr("style");
		assertTrue(style == null || !style.contains("padding"),
				"default gap (4px) must not emit redundant inline padding");
	}

	@Test
	public void gap_zero_emits_zero_padding() {
		connect();
		waitResponse();
		String style = jq("$av-gap-0 .z-avatar-text").attr("style");
		assertTrue(style != null && style.contains("padding:0 0px"),
				"gap=0 must emit padding:0 0px — got: " + style);
	}

	@Test
	public void gap_custom_emits_padding_in_pixels() {
		connect();
		waitResponse();
		String style = jq("$av-gap-8 .z-avatar-text").attr("style");
		assertTrue(style != null && style.contains("padding:0 8px"),
				"gap=8 must emit padding:0 8px — got: " + style);
	}

	@Test
	public void gap_dynamic_change() {
		connect();
		waitResponse();
		click(jq("$btn-set-gap"));
		waitResponse();
		String style = jq("$avDyn .z-avatar-text").attr("style");
		assertTrue(style != null && style.contains("padding:0 12px"),
				"setGap(12) must update inline padding to 12px — got: " + style);
	}

	@Test
	public void gap_negative_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-gap"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("gap must be non-negative"),
				"negative gap should throw — got: " + err);
	}

	// ----- accessibility -----

	@Test
	public void aria_role_img() {
		// Initials mode: outer <span role="img" aria-label="..."> IS the
		// SR-visible image element since the visible text is just initials.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("img", jq("$av-default").attr("role"),
				"initials-mode avatar must declare role=img for screen readers");
	}

	@Test
	public void aria_initials_mode_has_aria_label() {
		// aria-label falls through getLabel(), so SR users hear the same
		// identifier as sighted users see in initials.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		String label = jq("$av-label").attr("aria-label");
		assertEquals("AL", label,
				"initials avatar must expose its label via aria-label");
	}

	@Test
	public void aria_image_mode_no_outer_role() {
		// Image mode: the inner <img alt> is the SR-visible element. Outer
		// <span> must NOT have role=img to avoid double-role announcements.
		// jq.attr() may return the literal string "null" when an attribute
		// is absent, so check value semantically rather than for null.
		connect();
		waitResponse();
		String role = jq("$av-img").attr("role");
		assertFalse("img".equals(role),
				"image-mode avatar outer span must not have role=img — got: " + role);
		assertTrue(jq("$av-img").find("img").exists(),
				"image-mode avatar must render the <img> element");
	}

	@Test
	public void gap_out_of_range_clamps_client_side() {
		// Avatar.ts#setGap mirrors the Java 0..24 bound by clamping + warning
		// (client-lenient) rather than throwing. An out-of-range gap must
		// render the clamped padding, not the raw value.
		connect();
		waitResponse();
		getEval("(zk.Widget.$(jq('$av-gap-8')[0]).setGap(100),'')");
		sleep(60);
		String style = jq("$av-gap-8").find(".z-avatar-text").attr("style");
		assertTrue(style != null && style.contains("24px"),
				"setGap(100) must clamp to 24px — got: " + style);
		assertFalse(style.contains("100px"),
				"raw out-of-range gap must not be rendered");
	}

	@Test
	public void custom_zclass_is_honored_client_side() {
		// getZclass() must return a server/client-set custom zclass instead of
		// the hardcoded "z-avatar" default.
		connect();
		waitResponse();
		assertEquals("z-avatar",
				getEval("zk.Widget.$(jq('$av-label')[0]).getZclass()"),
				"default zclass");
		assertEquals("z-my-avatar",
				getEval("zk.Widget.$(jq('$av-label')[0]).setZclass('z-my-avatar').getZclass()"),
				"custom zclass must be honored, not the hardcoded default");
	}
}
