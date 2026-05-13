/* F104_ZK_6097_BreadcrumbTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:08:07 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_BreadcrumbTest extends WebDriverTestCase {

	// ----- structure / counts -----

	@Test
	public void renders_n_items() {
		connect();
		waitResponse();
		assertEquals(3, jq("$bc1 .z-breadcrumbitem").length());
	}

	@Test
	public void renders_n_minus_1_separators() {
		connect();
		waitResponse();
		assertEquals(2, jq("$bc1 .z-breadcrumb-separator").length(),
				"3 items must produce 2 separators");
	}

	@Test
	public void single_item_has_no_separator() {
		connect();
		waitResponse();
		assertEquals(1, jq("$bc-single .z-breadcrumbitem").length());
		assertFalse(jq("$bc-single .z-breadcrumb-separator").exists(),
				"a single-item breadcrumb has no separator");
	}

	@Test
	public void empty_breadcrumb_renders_empty_list() {
		connect();
		waitResponse();
		assertTrue(jq("$bc-empty").exists(), "empty breadcrumb root still renders");
		assertEquals(0, jq("$bc-empty .z-breadcrumbitem").length());
		assertFalse(jq("$bc-empty .z-breadcrumb-separator").exists());
	}

	// ----- href / target / disabled (Breadcrumbitem) -----

	@Test
	public void item_with_href_renders_anchor() {
		connect();
		waitResponse();
		assertTrue(jq("$bci1 a").exists(), "item with href renders <a>");
		assertEquals("Home", jq("$bci1 a").text());
	}

	@Test
	public void item_without_href_renders_span() {
		connect();
		waitResponse();
		assertFalse(jq("$bci3 a").exists(), "item without href has no <a>");
		assertTrue(jq("$bci3 span").exists(), "item without href renders <span>");
		assertEquals("ZK Framework", jq("$bci3 span").text());
	}

	@Test
	public void item_href_is_run_through_encode_url() {
		connect();
		waitResponse();
		// Breadcrumbitem must encode the href via Execution.encodeURL like
		// A/Button/Menuitem; a raw href would leave the "~./" resource prefix
		// unresolved (and would also skip context-path / cookieless rewriting).
		String href = jq("$bciEnc a").attr("href");
		assertTrue(jq("$bciEnc a").exists(), "encoded item still renders an <a>");
		assertFalse(href.contains("~."),
				"href must be resolved by Execution.encodeURL, got: " + href);
	}

	@Test
	public void item_with_target_propagates_to_anchor() {
		connect();
		waitResponse();
		assertEquals("_blank", jq("$bci-target a").attr("target"),
				"target attribute must propagate to the rendered <a>");
	}

	@Test
	public void item_disabled_renders_span_not_anchor() {
		connect();
		waitResponse();
		assertTrue(jq("$bciDis").hasClass("z-breadcrumbitem-disabled"));
		assertFalse(jq("$bciDis a").exists(),
				"disabled item must not render an <a> even if href is set");
	}

	@Test
	public void item_disabled_dynamic_toggle() {
		// setDisabled triggers a full rerender so the <a> vs <span> choice
		// in the mold is re-evaluated against (_href && !_disabled).
		connect();
		waitResponse();
		assertTrue(jq("$bciDis").hasClass("z-breadcrumbitem-disabled"));
		assertFalse(jq("$bciDis a").exists(),
				"disabled item starts as <span>");
		click(jq("$btn-toggle-disabled"));
		waitResponse();
		assertFalse(jq("$bciDis").hasClass("z-breadcrumbitem-disabled"),
				"toggling disabled false should drop the class");
		assertTrue(jq("$bciDis a").exists(),
				"after enabling, <a> must be re-rendered (rerender on setDisabled)");
	}

	// ----- separator -----

	@Test
	public void separator_default_slash() {
		connect();
		waitResponse();
		assertEquals("/", jq("$bc1 .z-breadcrumb-separator:first").text());
	}

	@Test
	public void separator_custom_string_renders_verbatim() {
		connect();
		waitResponse();
		assertEquals(">", jq("$bc2 .z-breadcrumb-separator:first").text());
	}

	@Test
	public void separator_dynamic_change() {
		connect();
		waitResponse();
		click(jq("$btn-set-sep"));
		waitResponse();
		assertEquals("|", jq("$bc1 .z-breadcrumb-separator:first").text());
		assertEquals(3, jq("$bc1 .z-breadcrumbitem").length(),
				"items must remain stable after separator change");
	}

	@Test
	public void separator_empty_falls_back_to_default() {
		connect();
		waitResponse();
		click(jq("$btn-set-sep"));    // first set to |
		waitResponse();
		click(jq("$btn-clear-sep"));  // then clear (empty/null) → default
		waitResponse();
		assertEquals("/", jq("$bc1 .z-breadcrumb-separator:first").text(),
				"setSeparator(empty) must fall back to '/' default");
	}

	// ----- accessibility -----

	@Test
	public void aria_last_item_has_aria_current_page() {
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("page", jq("$bci3").attr("aria-current"),
				"the last item must carry aria-current=page");
	}

	@Test
	public void aria_non_last_items_have_no_aria_current() {
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals(1, jq("$bc1 [aria-current=page]").length(),
				"only the last item in a breadcrumb may declare aria-current=page");
	}

	@Test
	public void aria_separators_are_hidden_from_AT() {
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("true", jq("$bc1 .z-breadcrumb-separator:first").attr("aria-hidden"),
				"separators must declare aria-hidden=true");
	}

	@Test
	public void aria_root_is_nav_with_aria_label() {
		connect();
		waitResponse();
		String tag = getEval("jq('$bc1')[0].tagName.toLowerCase()");
		assertTrue("nav".equals(tag) || tag.contains("nav"),
				"breadcrumb root tag should be <nav> — got: " + tag);
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("breadcrumb", jq("$bc1").attr("aria-label"));
	}

	// ----- maxItems collapse -----

	@Test
	public void max_items_collapse_renders_ellipsis() {
		connect();
		waitResponse();
		// 6 items, maxItems=3 → first + ellipsis + last 2 visible.
		assertTrue(jq("$bc-collapse .z-breadcrumb-ellipsis").exists(),
				"maxItems<count must render an ellipsis (…) node");
	}

	@Test
	public void max_items_hides_middle_items() {
		connect();
		waitResponse();
		// keepStart=1 + keepEnd=2 → items 2..4 (1-indexed: bcc2..bcc4) hidden
		assertEquals("none", jq("$bcc2").css("display"),
				"bcc2 (middle item) must be display:none under maxItems=3");
		assertEquals("none", jq("$bcc3").css("display"),
				"bcc3 (middle item) must be display:none under maxItems=3");
		assertEquals("none", jq("$bcc4").css("display"),
				"bcc4 (middle item) must be display:none under maxItems=3");
	}

	@Test
	public void max_items_keeps_first_and_last_items_visible() {
		connect();
		waitResponse();
		// bcc1 (first) and bcc5, bcc6 (last 2) must stay visible
		String d1 = jq("$bcc1").css("display");
		assertTrue(d1 == null || !"none".equals(d1),
				"bcc1 (first kept) must be visible, got display: " + d1);
		String d5 = jq("$bcc5").css("display");
		assertTrue(d5 == null || !"none".equals(d5),
				"bcc5 (kept) must be visible, got display: " + d5);
		String d6 = jq("$bcc6").css("display");
		assertTrue(d6 == null || !"none".equals(d6),
				"bcc6 (kept, last) must be visible, got display: " + d6);
	}

	@Test
	public void count_below_max_does_not_collapse() {
		connect();
		waitResponse();
		// bc1 has 3 items, no maxItems set → no ellipsis
		assertFalse(jq("$bc1 .z-breadcrumb-ellipsis").exists(),
				"breadcrumb under maxItems threshold must not collapse");
	}

	// ----- icon-form separator (already supported by mold) -----

	@Test
	public void icon_separator_renders_as_icon_element() {
		connect();
		waitResponse();
		// separator="icon:z-icon-chevron-right" → <i class="z-icon-chevron-right">
		assertTrue(jq("$bc-icon-sep").find(".z-breadcrumb-separator > i.z-icon-chevron-right").exists(),
				"icon: prefix on separator must render as an <i> element");
	}

	// ----- maxItems negative-path validation -----

	@Test
	public void max_items_negative_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-maxitems"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("maxItems cannot be negative"),
				"setMaxItems(-1) must throw WrongValueException — got: " + err);
	}

	@Test
	public void max_items_one_throws() {
		// maxItems=1 cannot collapse below the first+last pair, so it's a
		// semantic no-op. Reject it explicitly instead of silently rendering
		// 2 items.
		connect();
		waitResponse();
		click(jq("$btn-maxitems-one"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("maxItems must be 0"),
				"setMaxItems(1) must throw — got: " + err);
	}

	// ----- Breadcrumbitem.setHref scheme allow-list -----

	@Test
	public void href_javascript_via_tab_throws() {
		// "java\tscript:..." would slip past trim().toLowerCase().startsWith(
		// "javascript:") but browsers strip 0x09/0x0A/0x0D from URL schemes
		// before parsing, so the resulting <a> would still execute. The
		// server must strip ASCII controls before the prefix check.
		connect();
		waitResponse();
		click(jq("$btn-href-tab-bypass"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("Unsafe href scheme"),
				"setHref(\"java\\tscript:alert(1)\") must be rejected — got: " + err);
	}

	@Test
	public void href_data_image_svg_throws() {
		// data:image/svg+xml is technically an image MIME but SVG permits
		// inline <script> / <svg onload=…> and is a known XSS vector — the
		// allow-list must exclude it explicitly even though it matches the
		// "data:image/" prefix.
		connect();
		waitResponse();
		click(jq("$btn-href-svg-xss"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("Unsafe href scheme"),
				"setHref(\"data:image/svg+xml;...\") must be rejected — got: " + err);
	}

	// ----- collapsed-ellipsis is an interactive <button> -----

	@Test
	public void ellipsis_is_real_button() {
		// Passive <li> can't be tabbed to or activated by keyboard. The
		// expand affordance must be a native <button> so AT exposes it
		// and Enter/Space activate it. Use querySelector inside eval to
		// avoid relying on jq's compound-selector parsing.
		connect();
		waitResponse();
		String btnTag = getEval(
				"(function(){"
				+ " var li = jq('$bc-collapse')[0].querySelector('.z-breadcrumb-ellipsis');"
				+ " if (!li) return 'NO-ELLIPSIS-LI';"
				+ " var btn = li.querySelector('button');"
				+ " return btn ? btn.tagName : 'NO-BUTTON';"
				+ "})()");
		assertEquals("BUTTON", btnTag,
				"collapsed ellipsis must contain a <button> — got: " + btnTag);
	}

	@Test
	public void ellipsis_click_reveals_hidden_items() {
		// Clicking the ellipsis button should reveal the collapsed items.
		// Verified by checking display:none is cleared after the click.
		connect();
		waitResponse();
		assertEquals("none", jq("$bcc2").css("display"),
				"baseline: bcc2 should be hidden under maxItems=3");
		String result = getEval(
				"(function(){"
				+ " var btn = jq('$bc-collapse')[0].querySelector('.z-breadcrumb-ellipsis button');"
				+ " if (!btn) return 'NO-BUTTON';"
				+ " btn.click();"
				+ " return jq('$bcc2').css('display');"
				+ "})()");
		assertTrue(result != null && !"none".equals(result) && !"NO-BUTTON".equals(result),
				"clicking ellipsis must reveal collapsed items — got: " + result);
	}
}
