/* F110_ZK_6097_CarouselTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:08:11 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F110_ZK_6097_CarouselTest extends WebDriverTestCase {

	// ----- structure -----

	@Test
	public void renders_all_carousel_items() {
		connect();
		waitResponse();
		assertEquals(3, jq("$cr1").find(".z-carouselitem").length());
	}

	@Test
	public void first_item_active_by_default() {
		connect();
		waitResponse();
		assertTrue(jq("$ci0").hasClass("z-carouselitem-active"));
		assertFalse(jq("$ci1").hasClass("z-carouselitem-active"));
	}

	// ----- showArrows / showIndicators -----

	@Test
	public void arrows_rendered_by_default() {
		connect();
		waitResponse();
		assertTrue(jq("$cr1 .z-carousel-arrow-prev").exists());
		assertTrue(jq("$cr1 .z-carousel-arrow-next").exists());
	}

	@Test
	public void indicators_rendered_by_default_one_per_slide() {
		connect();
		waitResponse();
		assertEquals(3, jq("$cr1").find(".z-carousel-indicator").length());
	}

	@Test
	public void show_arrows_false_hides_arrow_buttons() {
		connect();
		waitResponse();
		assertFalse(jq("$cr-noarrows .z-carousel-arrow-prev").exists());
		assertFalse(jq("$cr-noarrows .z-carousel-arrow-next").exists());
	}

	@Test
	public void show_indicators_false_hides_indicators() {
		connect();
		waitResponse();
		assertFalse(jq("$cr-noarrows .z-carousel-indicators").exists());
	}

	@Test
	public void single_child_suppresses_arrows_and_indicators() {
		connect();
		waitResponse();
		assertFalse(jq("$cr-single .z-carousel-arrow-prev").exists(),
				"a single-child carousel must not render navigation arrows");
		assertFalse(jq("$cr-single .z-carousel-indicators").exists(),
				"a single-child carousel must not render indicators");
	}

	// ----- orient -----

	@Test
	public void orient_horizontal_default_no_vertical_class() {
		connect();
		waitResponse();
		assertFalse(jq("$cr1").hasClass("z-carousel-vertical"));
	}

	@Test
	public void orient_vertical_class_applied() {
		connect();
		waitResponse();
		assertTrue(jq("$cr-vertical").hasClass("z-carousel-vertical"));
	}

	@Test
	public void orient_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		click(jq("$btn-bad-orient"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("orient must be"),
				"invalid orient should surface a WrongValueException — got: " + err);
		assertFalse(jq("$cr1").hasClass("z-carousel-vertical"),
				"invalid orient must not mutate the rendered class");
	}

	// ----- effect -----

	@Test
	public void effect_slide_default_no_effect_class() {
		connect();
		waitResponse();
		assertFalse(jq("$cr1").hasClass("z-carousel-effect-fade"));
		assertFalse(jq("$cr1").hasClass("z-carousel-effect-none"));
	}

	@Test
	public void effect_fade_class_applied() {
		connect();
		waitResponse();
		assertTrue(jq("$cr-vertical").hasClass("z-carousel-effect-fade"));
	}

	@Test
	public void effect_none_class_applied() {
		connect();
		waitResponse();
		assertTrue(jq("$cr-none").hasClass("z-carousel-effect-none"));
	}

	@Test
	public void effect_none_navigation_moves_the_track() {
		// ZK-6097 P2-1: effect="none" must still translate the track to bring
		// the active slide into the overflow-clipped viewport (an instant jump,
		// no animation). A prior _applyTrackPosition early-return that excluded
		// every non-'slide' effect froze 'none' permanently on slide 0.
		connect();
		waitResponse();
		click(jq("$cr-none").find(".z-carousel-arrow-next"));
		waitResponse();
		String tf = getEval("jq('$cr-none').find('.z-carousel-track')[0].style.transform");
		assertTrue(tf != null && tf.contains("-100%"),
				"effect=none 'next' must translate the track to slide 1; got: " + tf);
	}

	@Test
	public void effect_invalid_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-effect"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("effect must be"),
				"invalid effect should surface a WrongValueException — got: " + err);
	}

	// ----- arrows / indicators interaction -----

	@Test
	public void click_next_advances_one_step_and_fires_onSelect() {
		connect();
		waitResponse();
		click(jq("$cr1").find(".z-carousel-arrow-next"));
		waitResponse();
		assertEquals("i=1", jq("$selResult").text(), "onSelect must report new index");
		assertTrue(jq("$ci1").hasClass("z-carouselitem-active"));
		assertFalse(jq("$ci0").hasClass("z-carouselitem-active"));
	}

	@Test
	public void click_prev_from_first_with_loop_wraps_to_last() {
		connect();
		waitResponse();
		click(jq("$cr1").find(".z-carousel-arrow-prev"));
		waitResponse();
		assertEquals("i=2", jq("$selResult").text(),
				"prev from index 0 with loop=true must wrap to last");
		assertTrue(jq("$ci2").hasClass("z-carouselitem-active"));
	}

	@Test
	public void click_indicator_jumps_to_that_slide() {
		connect();
		waitResponse();
		click(jq("$cr1").find(".z-carousel-indicator:eq(2)"));
		waitResponse();
		assertEquals("i=2", jq("$selResult").text());
		assertTrue(jq("$ci2").hasClass("z-carouselitem-active"));
	}

	@Test
	public void next_from_last_with_loop_wraps_to_first() {
		connect();
		waitResponse();
		click(jq("$cr1").find(".z-carousel-indicator:eq(2)"));
		waitResponse();
		click(jq("$cr1 .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("i=0", jq("$selResult").text());
		assertTrue(jq("$ci0").hasClass("z-carouselitem-active"));
	}

	// ----- loop -----

	@Test
	public void loop_false_next_from_last_stays_on_last() {
		connect();
		waitResponse();
		// cr-noloop starts at activeIndex=1 (the last slide)
		assertTrue(jq("$cinl1").hasClass("z-carouselitem-active"));
		click(jq("$cr-noloop .z-carousel-arrow-next"));
		waitResponse();
		assertTrue(jq("$cinl1").hasClass("z-carouselitem-active"),
				"loop=false: next from last must not wrap");
	}

	@Test
	public void loop_false_prev_from_first_stays_on_first() {
		connect();
		waitResponse();
		// move cr-noloop to index 0 first
		click(jq("$cr-noloop .z-carousel-arrow-prev"));
		waitResponse();
		assertTrue(jq("$cinl0").hasClass("z-carouselitem-active"));
		// prev again — should stay
		click(jq("$cr-noloop .z-carousel-arrow-prev"));
		waitResponse();
		assertTrue(jq("$cinl0").hasClass("z-carouselitem-active"),
				"loop=false: prev from first must not wrap");
	}

	// ----- activeIndex -----

	@Test
	public void server_setActiveIndex_changes_slide() {
		connect();
		waitResponse();
		click(jq("$btn-next-via-server"));
		waitResponse();
		assertTrue(jq("$ci1").hasClass("z-carouselitem-active"));
	}

	@Test
	public void activeIndex_negative_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-active"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("Out of bound: -1"),
				"negative activeIndex must throw — got: " + err);
		assertTrue(jq("$ci0").hasClass("z-carouselitem-active"),
				"failed setActiveIndex must not mutate active slide");
	}

	@Test
	public void activeIndex_negative_throws_before_the_first_render() {
		// The bound check runs against a child count the ZUL page has not filled
		// yet, so the upper bound is deferred — but a negative index is wrong at
		// any point, and renderProperties never re-validates it.
		connect();
		waitResponse();
		click(jq("$btn-unrendered-negative"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("Out of bound: -1"),
				"an unrendered carousel must still reject a negative index — got: " + err);
	}

	@Test
	public void activeIndex_above_child_count_is_kept_before_the_first_render() {
		// The other half of the same guard: with no children attached there is
		// nothing to compare against, so the value is stored and renderProperties
		// clamps what it sends to the client.
		connect();
		waitResponse();
		click(jq("$btn-unrendered-overflow"));
		waitResponse();
		assertEquals("accepted", jq("$errMsg").text(),
				"an unrendered carousel must accept an index above its (empty) child list");
	}

	@Test
	public void activeIndex_above_child_count_throws() {
		// Rejected once the carousel has rendered, so a stale index cannot leave
		// the server pointing at a slide the client never shows. A ZUL page that
		// sets activeIndex before its slides is the pre-render case and is
		// clamped by renderProperties instead, not rejected here.
		connect();
		waitResponse();
		click(jq("$btn-overflow-active"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("Out of bound: 99 while size=3"),
				"an index past the last slide must throw — got: " + err);
		assertTrue(jq("$ci0").hasClass("z-carouselitem-active"),
				"failed setActiveIndex must not mutate active slide");
	}

	// ----- autoplay + interval -----

	@Test
	public void autoplay_advances_after_interval() {
		// Race-tolerant: with interval=500ms (the API minimum) and loop=false,
		// by the time we observe the carousel autoplay has parked it at index
		// 1 regardless of initial timing. We assert end state, not initial.
		connect();
		waitResponse();
		try {
			Thread.sleep(1200);
		} catch (InterruptedException ignored) {}
		waitResponse();
		assertTrue(jq("$cap1").hasClass("z-carouselitem-active"),
				"autoplay must advance the active slide to the last index on its own");
		assertFalse(jq("$cap0").hasClass("z-carouselitem-active"));
	}

	// ----- pause (Bootstrap-style hover-pauses-autoplay) -----

	@Test
	public void pause_default_true_hover_clears_timer() {
		// Direct behavioural check: invoking the hover handler on a paused
		// carousel must clear the autoplay setInterval handle. Timing-based
		// tests are flaky because the test machine's wallclock between
		// connect() and the eval is unpredictable.
		connect();
		waitResponse();
		String result = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cr-auto')[0]);"
				+ " w._onHoverEnter();"
				+ " return w._timerId === undefined;"
				+ "})()");
		assertEquals("true", result,
				"hover with pause=true must clear the autoplay timer handle");
	}

	@Test
	public void pause_false_hover_keeps_timer() {
		connect();
		waitResponse();
		String result = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cr-nopause')[0]);"
				+ " w._onHoverEnter();"
				+ " return w._timerId !== undefined;"
				+ "})()");
		assertEquals("true", result,
				"hover with pause=false must leave the autoplay timer running");
	}

	@Test
	public void pause_default_true_hover_leave_resumes_timer() {
		connect();
		waitResponse();
		String result = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cr-auto')[0]);"
				+ " w._onHoverEnter();"
				+ " var pausedCleared = (w._timerId === undefined);"
				+ " w._onHoverLeave();"
				+ " return pausedCleared && (w._timerId !== undefined);"
				+ "})()");
		assertEquals("true", result,
				"after mouse leaves, the autoplay timer must resume");
	}

	// ----- focus indicator (WCAG 2.1 SC 2.4.7 / 1.4.11) -----

	// Driven with a real Tab, not element.focus(): :focus-visible does not match a
	// programmatic focus, so a JS-focused element reports no ring and the
	// assertion would fail against correct CSS.

	@Test
	public void keyboard_focus_paints_a_ring_on_the_tabbable_root() {
		connect();
		waitResponse();
		tabTo("$cr1");
		JQuery root = jq("$cr1");
		assertAll(
				// The root is tabindex="0" for arrow nav, so it owes its own indicator.
				() -> assertEquals("2px", root.css("outline-width"),
						"the focused carousel root paints no outline"),
				() -> assertNotEquals("none", root.css("outline-style"),
						"the focused carousel root paints no outline"),
				// Drawn outside the box: a positioned .z-carouselitem paints over an
				// inset ring, and the theme's global *:focus box-shadow is one.
				() -> assertEquals("2px", root.css("outline-offset"),
						"a non-positive offset puts the ring under the active slide"),
				() -> assertEquals("none", root.css("box-shadow"),
						"the theme's *:focus box-shadow must be cleared, or the root"
						+ " shows two stacked rings"));
	}

	@Test
	public void keyboard_focus_backs_the_arrow_and_dot_rings_for_contrast() {
		connect();
		waitResponse();
		// Both controls float over author media, so a single-tone ring has no
		// guaranteed contrast — each needs the base-colour backing behind it.
		tabTo("$cr1 .z-carousel-arrow-next");
		assertNotEquals("none", jq("$cr1 .z-carousel-arrow-next").css("box-shadow"),
				"the focused arrow has no contrast backing behind its ring");
		tabTo("$cr1 .z-carousel-indicator:eq(0)");
		assertNotEquals("none", jq("$cr1 .z-carousel-indicator:eq(0)").css("box-shadow"),
				"the focused indicator dot has no contrast backing behind its ring");
	}

	/** Tabs from the document start until {@code selector} holds focus. */
	private void tabTo(String selector) {
		getActions().sendKeys(Keys.TAB).perform();
		for (int i = 0; i < 40; i++) {
			if (Boolean.parseBoolean(getEval(
					"jq('" + selector + "')[0] === document.activeElement")))
				return;
			getActions().sendKeys(Keys.TAB).perform();
		}
		throw new AssertionError("never reached " + selector + " by tabbing");
	}

	// ----- keyboard (Bootstrap-style arrow nav) -----

	@Test
	public void keyboard_default_arrow_right_advances() {
		connect();
		waitResponse();
		assertTrue(jq("$ckb0").hasClass("z-carouselitem-active"));
		eval("(function(){"
				+ " var el = jq('$cr-kbd')[0];"
				+ " var w = zk.Widget.$(el);"
				+ " w._onKeyDown({domEvent:{key:'ArrowRight'},stop:function(){}});"
				+ "})()");
		waitResponse();
		assertTrue(jq("$ckb1").hasClass("z-carouselitem-active"),
				"ArrowRight on focused carousel must advance one slide");
	}

	@Test
	public void keyboard_default_arrow_left_retreats() {
		connect();
		waitResponse();
		eval("(function(){"
				+ " var el = jq('$cr-kbd')[0];"
				+ " var w = zk.Widget.$(el);"
				+ " w._onKeyDown({domEvent:{key:'ArrowRight'},stop:function(){}});"
				+ " w._onKeyDown({domEvent:{key:'ArrowLeft'},stop:function(){}});"
				+ "})()");
		waitResponse();
		assertTrue(jq("$ckb0").hasClass("z-carouselitem-active"),
				"ArrowLeft after ArrowRight must restore the previous slide");
	}

	@Test
	public void keyboard_false_arrow_keys_ignored() {
		connect();
		waitResponse();
		assertTrue(jq("$cnk0").hasClass("z-carouselitem-active"));
		eval("(function(){"
				+ " var el = jq('$cr-nokbd')[0];"
				+ " var w = zk.Widget.$(el);"
				+ " w._onKeyDown({domEvent:{key:'ArrowRight'},stop:function(){}});"
				+ "})()");
		waitResponse();
		assertTrue(jq("$cnk0").hasClass("z-carouselitem-active"),
				"keyboard=false must ignore arrow keys");
	}

	// ----- accessibility -----

	@Test
	public void aria_role_region() {
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("region", jq("$cr1").attr("role"));
	}

	// ----- status announcer stays visually hidden -----

	@Test
	public void status_announcer_is_visually_hidden() {
		// Hidden by carousel's own LESS, not the framework .sr-only — that class
		// lives in the theme-resolved ~./zul/font/ and is absent under a theme.
		// Assert the painted result, not the class name.
		connect();
		waitResponse();
		assertAll(
				() -> assertEquals("absolute", announcerStyle("position"),
						"announcer is not taken out of flow"),
				() -> assertEquals("1px", announcerStyle("width"),
						"announcer is not clamped to 1px wide"),
				() -> assertEquals("1px", announcerStyle("height"),
						"announcer is not clamped to 1px tall"),
				() -> assertEquals("hidden", announcerStyle("overflow"),
						"announcer does not clip its overflow"),
				() -> assertTrue(announcerStyle("clip").startsWith("rect("),
						"announcer has no clipping rect — got: " + announcerStyle("clip")));
	}

	@Test
	public void status_announcer_text_does_not_paint() {
		// Paint-level: put a status string in the announcer and measure the box
		// it actually occupies. Unhidden, an inline span grows to the width of
		// "Slide 1 of 3"; hidden, it stays the 1x1 sr-only box.
		connect();
		waitResponse();
		String box = announcerBoxWithText("Slide 1 of 3");
		String[] wh = box.split("x");
		assertAll(
				() -> assertTrue(Double.parseDouble(wh[0]) <= 2,
						"announcer text paints — box is " + box + ", expected ~1x1"),
				() -> assertTrue(Double.parseDouble(wh[1]) <= 2,
						"announcer text paints — box is " + box + ", expected ~1x1"));
	}

	@Test
	public void za11y_status_text_stays_hidden() {
		// End-to-end: za11y really did write "Slide N of M" into the node, and
		// the node is still the 1x1 sr-only box while holding it.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		String text = getEval("jq('$cr1').find('.z-carousel-status')[0].textContent");
		assertTrue(text != null && !text.trim().isEmpty(),
				"za11y must write a status string into the announcer — got: " + text);
		String box = getEval("(function(){"
				+ " var r = jq('$cr1').find('.z-carousel-status')[0].getBoundingClientRect();"
				+ " return r.width + 'x' + r.height;"
				+ "})()");
		String[] wh = box.split("x");
		assertAll(
				() -> assertTrue(Double.parseDouble(wh[0]) <= 2,
						"za11y status text paints — box is " + box),
				() -> assertTrue(Double.parseDouble(wh[1]) <= 2,
						"za11y status text paints — box is " + box));
	}

	private String announcerStyle(String cssProp) {
		return getEval("getComputedStyle(jq('$cr1').find('.z-carousel-status')[0])." + cssProp);
	}

	/** Writes {@code text} into the announcer, measures its box, then restores it. */
	private String announcerBoxWithText(String text) {
		return getEval("(function(){"
				+ " var n = jq('$cr1').find('.z-carousel-status')[0],"
				+ "     old = n.textContent;"
				+ " n.textContent = '" + text + "';"
				+ " var r = n.getBoundingClientRect(),"
				+ "     box = r.width + 'x' + r.height;"
				+ " n.textContent = old;"
				+ " return box;"
				+ "})()");
	}

	@Test
	public void aria_arrow_buttons_have_aria_label() {
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		String prev = jq("$cr1 .z-carousel-arrow-prev").attr("aria-label");
		String next = jq("$cr1 .z-carousel-arrow-next").attr("aria-label");
		assertTrue(prev != null && !prev.isEmpty(),
				"prev arrow must have aria-label");
		assertTrue(next != null && !next.isEmpty(),
				"next arrow must have aria-label");
	}

	// ----- seamless loop (cloned head/tail slides) -----

	@Test
	public void slide_loop_installs_two_clones_in_track() {
		connect();
		waitResponse();
		// cr1: effect=slide (default), loop=true (default), 3 real items
		// → head clone appended, tail clone prepended.
		assertEquals(2, jq("$cr1").find(".z-carousel-track > .z-carousel-clone").length(),
				"effect=slide + loop=true must install exactly two clones");
		// .z-carouselitem selector must still see only real items
		assertEquals(3, jq("$cr1").find(".z-carouselitem").length(),
				"clones must not show up under the .z-carouselitem selector");
	}

	@Test
	public void fade_does_not_install_clones() {
		connect();
		waitResponse();
		// cr-vertical: effect=fade — fade has its own absolute-positioned
		// layout and doesn't need the cloning trick.
		assertFalse(jq("$cr-vertical .z-carousel-clone").exists(),
				"effect=fade must not install seamless-loop clones");
	}

	@Test
	public void loop_false_does_not_install_clones() {
		connect();
		waitResponse();
		// cr-noloop: loop=false — no wrap, no clones.
		assertFalse(jq("$cr-noloop .z-carousel-clone").exists(),
				"loop=false must not install seamless-loop clones");
	}

	@Test
	public void single_child_does_not_install_clones() {
		connect();
		waitResponse();
		assertFalse(jq("$cr-single .z-carousel-clone").exists(),
				"single-child carousel has nothing to loop, must not clone");
	}

	// ----- onChanging event (pre-change hook) -----

	@Test
	public void changing_event_fires_before_select_with_from_and_to() {
		connect();
		waitResponse();
		click(jq("$cr1 .z-carousel-arrow-next"));
		waitResponse();
		assertEquals("from=0 to=1", jq("$changingResult").text(),
				"onChanging must carry fromIndex + toIndex");
		assertEquals("i=1", jq("$selResult").text(),
				"onSelect must still fire after onChanging");
	}

	// ----- touch / swipe (Pointer Events) -----

	@Test
	public void track_has_pan_y_touch_action_for_horizontal_swipe() {
		connect();
		waitResponse();
		// touch-action must reserve the horizontal axis for our swipe handler
		// (or vertical, when orient='vertical') — otherwise iOS / Android eat
		// the gesture as a page scroll.
		String css = jq("$cr1").find(".z-carousel-track").css("touch-action");
		assertTrue(css != null && css.contains("pan-y"),
				"horizontal carousel track must declare touch-action: pan-y, got: " + css);
	}

	@Test
	public void vertical_carousel_track_has_pan_x_touch_action() {
		connect();
		waitResponse();
		String css = jq("$cr-vertical").find(".z-carousel-track").css("touch-action");
		// effect=fade on cr-vertical means pointer listeners aren't installed,
		// but the CSS rule applies via the .z-carousel-vertical selector. We
		// only assert the CSS contract here, not the pointer wiring.
		assertTrue(css != null && css.contains("pan-x"),
				"vertical carousel track must declare touch-action: pan-x, got: " + css);
	}

	@Test
	public void native_image_drag_on_a_slide_is_suppressed() {
		connect();
		waitResponse();
		// swipe_on_a_photo_slide_changes_slide drives the whole gesture; this one
		// pins the guard itself, so a regression names its own cause instead of
		// showing up as an unchanged activeIndex.
		String result = getEval("(function(){"
				+ "var d = new DragEvent('dragstart', {bubbles:true, cancelable:true});"
				+ "jq('$ci0').find('img')[0].dispatchEvent(d);"
				+ "return String(d.defaultPrevented);"
				+ "})()");
		assertEquals("true", result,
				"dragstart on a slide photo must be cancelled, or the native image "
				+ "drag takes over and pointercancel aborts the slide change");
	}

	@Test
	public void swipe_on_a_photo_slide_changes_slide() {
		connect();
		waitResponse();
		// The reported gesture end to end: press on the slide's photo and drag
		// left past the threshold. Unguarded, the browser claims the gesture as
		// a native image drag and fires pointercancel within the first few px,
		// so the track snaps home and the slide never changes. Stay on the
		// horizontal axis — the release point plays no part, and a diagonal
		// would land on the autoplaying carousel below.
		getActions().moveToElement(toElement(jq("$ci0").find("img")))
				.clickAndHold()
				.moveByOffset(-45, 0)
				.moveByOffset(-45, 0)
				.moveByOffset(-45, 0)
				.release().perform();
		waitResponse();
		assertEquals("1", getEval("zk.Widget.$(jq('$cr1')[0])._activeIndex"),
				"a swipe starting on a slide photo must still advance the carousel");
	}

	@Test
	public void dragstart_on_a_text_field_inside_a_slide_is_left_alone() {
		connect();
		waitResponse();
		// zk.Draggable's own carve-out: a text field keeps its native drag, so
		// selected text can still be dragged out of a slide. The field is made
		// here rather than on the page — the guard only walks up from the drag
		// target looking for a control, it reads nothing else about the slide.
		String result = getEval("(function(){"
				+ "var slide = jq('$ci0')[0], inp = document.createElement('input');"
				+ "slide.appendChild(inp);"
				+ "var d = new DragEvent('dragstart', {bubbles:true, cancelable:true});"
				+ "inp.dispatchEvent(d);"
				+ "var prevented = d.defaultPrevented;"
				+ "slide.removeChild(inp);"
				+ "return String(prevented);"
				+ "})()");
		assertEquals("false", result,
				"dragstart on a text field inside a slide must not be cancelled");
	}

	@Test
	public void dragstart_guard_is_removed_when_the_effect_leaves_slide() {
		connect();
		waitResponse();
		// Everything is unregistered by function reference; removeEventListener
		// or jq().off() handed any other reference would silently leave it
		// attached. Assert both halves: _onPointerDown has no _effect check of
		// its own, so a surviving pointerdown listener would still drag a fade
		// carousel's track.
		String result = getEval("(function(){"
				+ "var w = zk.Widget.$(jq('$cr1')[0]);"
				+ "w.setEffect('fade');"
				+ "var d = new DragEvent('dragstart', {bubbles:true, cancelable:true});"
				+ "jq('$ci0').find('img')[0].dispatchEvent(d);"
				+ "w.$n('track').dispatchEvent(new PointerEvent('pointerdown', {bubbles:true,"
				+ " cancelable:true, pointerId:1, pointerType:'mouse', button:0,"
				+ " buttons:1, clientX:100, clientY:100}));"
				+ "return d.defaultPrevented + ',' + w._dragging;"
				+ "})()");
		assertEquals("false,false", result,
				"every drag listener must come off when effect leaves 'slide' — got "
				+ "dragstartPrevented,dragging = " + result);
	}

	@Test
	public void slide_track_suppresses_text_selection() {
		connect();
		waitResponse();
		// Without this the swipe paints a text selection across the slides.
		String css = jq("$cr1").find(".z-carousel-track").css("user-select");
		assertEquals("none", css,
				"swipeable carousel track must declare user-select: none, got: " + css);
	}

	@Test
	public void non_swipeable_track_keeps_text_selectable() {
		connect();
		waitResponse();
		// cr-none is horizontal like cr1 and differs only in effect, so this
		// pins the rule to the effect and not to the orientation.
		String css = jq("$cr-none").find(".z-carousel-track").css("user-select");
		assertNotEquals("none", css,
				"a carousel with no swipe must not disable text selection, got: " + css);
	}

	// ----- interval lower-bound validation -----

	@Test
	public void interval_below_500_throws() {
		connect();
		waitResponse();
		click(jq("$btn-bad-interval"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("interval must be >= 500"),
				"setInterval(100) must throw WrongValueException — got: " + err);
	}

	// ----- aria-current on active slide -----

	@Test
	public void active_slide_has_aria_current() {
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		String ariaCurrent = jq("$cr1 .z-carouselitem-active").attr("aria-current");
		assertEquals("true", ariaCurrent,
				"active carousel slide must carry aria-current=\"true\"");
	}

	// ----- loop=false boundary disables next arrow -----

	@Test
	public void noloop_boundary_disables_next_arrow() {
		// cr-noloop starts at activeIndex=1 of 2 slides → at the end → "next"
		// arrow must carry the disabled attribute (silently early-returning
		// isn't enough; keyboard / AT users need the native disabled state).
		connect();
		waitResponse();
		String disabled = jq("$cr-noloop").find(".z-carousel-arrow-next").attr("disabled");
		assertTrue(disabled != null && (disabled.equals("disabled") || disabled.equals("true") || disabled.equals("")),
				"loop=false carousel at last slide must disable the next arrow — got: " + disabled);
	}

	// ----- Space key toggles autoplay -----

	@Test
	public void space_key_pauses_autoplaying_carousel() {
		// Direct behavioural check: invoking _onKeyDown with a synthesized
		// Space-key event on the autoplaying carousel must clear its timer.
		// Timing-based tests are flaky because the test machine's wallclock
		// between connect() and the eval is unpredictable.
		connect();
		waitResponse();
		String result = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cr-auto')[0]);"
				+ " var hadTimer = w._timerId !== undefined;"
				+ " w._onKeyDown({domEvent:{key:' '}, stop:function(){}});"
				+ " return hadTimer && (w._timerId === undefined);"
				+ "})()");
		assertEquals("true", result,
				"Space key on autoplaying carousel must stop the timer");
	}

	@Test
	public void setPause_false_resumes_hover_paused_autoplay() {
		// P3-2: a server setPause(false) arriving while the pointer rests on an
		// autoplaying carousel must resume auto-advance immediately. _onHoverLeave's
		// resume guard requires _pause, so without re-syncing the timer inside
		// setPause the hover-pause would never lift until the next mouseenter/leave
		// cycle. Direct invocation (no timing) mirrors space_key_pauses_*.
		connect();
		waitResponse();
		String result = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cr-auto')[0]);"
				+ " var hadTimer = w._timerId !== undefined;"
				+ " w._onHoverEnter();"
				+ " var hoverPaused = (w._timerId === undefined);"
				+ " w.setPause(false);"
				+ " return hadTimer && hoverPaused && (w._timerId !== undefined);"
				+ "})()");
		assertEquals("true", result,
				"setPause(false) while hover-paused must resume the autoplay timer");
	}

	@Test
	public void autoplay_resumes_after_space_pause_then_server_reenable() {
		// 6-2: a Space-pause sets _userPaused; a later server-driven
		// setAutoplay(false)->(true) must clear that transient pause and
		// resume the timer (pre-fix, _startTimer early-returned on the stale
		// _userPaused so autoplay never recovered).
		connect();
		waitResponse();
		String result = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cr-auto')[0]);"
				+ " w._userPaused = true; w._stopTimer();"
				+ " var pausedStopped = (w._timerId === undefined);"
				+ " w.setAutoplay(false);"
				+ " w.setAutoplay(true);"
				+ " return pausedStopped && (w._userPaused === false)"
				+ "        && (w._timerId !== undefined);"
				+ "})()");
		assertEquals("true", result,
				"server setAutoplay(false->true) must clear a prior Space-pause and resume the timer");
	}

	@Test
	public void aria_label_author_supplied_via_ca_is_preserved() {
		// 6-11: a carousel region's accessible name is author-supplied via
		// ca:aria-label; the za11y augment must not clobber it.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("Featured shows", jq("$cr-aria").attr("aria-label"),
				"author-supplied ca:aria-label must survive the za11y augment");
	}

	@Test
	public void status_text_seeded_before_live_region_registration() {
		// The -status span is mold-rendered empty and carries no aria-live of
		// its own, so the initial "Slide N of M" must be written BEFORE bind_
		// turns the span into a live region — otherwise every bind announces a
		// slide change that never happened. The final DOM is identical either
		// way, so the only observable difference is the mutation ORDER.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		// rerender(-1) rebinds synchronously and takeRecords() drains the
		// observer queue synchronously — a MutationObserver callback would only
		// run as a microtask, i.e. after this expression already returned.
		String seq = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cr1')[0]);"
				+ " var host = w.$n().parentNode;"
				+ " var obs = new MutationObserver(function(){});"
				+ " obs.observe(host, {subtree:true, childList:true, characterData:true,"
				+ "   attributes:true, attributeFilter:['aria-live']});"
				+ " w.rerender(-1);"
				+ " var recs = obs.takeRecords();"
				+ " obs.disconnect();"
				+ " var st = w.$n('status'), seq = [];"
				+ " for (var i = 0; i < recs.length; i++) {"
				+ "  var r = recs[i];"
				+ "  if (r.type == 'attributes') { if (r.target === st) seq.push('live'); }"
				+ "  else if (r.target === st || r.target.parentNode === st) seq.push('text');"
				+ " }"
				+ " return seq.join(',');"
				+ "})()");
		int live = seq.indexOf("live");
		assertTrue(live >= 0,
				"the observer recorded no aria-live registration at all — got: " + seq);
		assertEquals(-1, seq.indexOf("text", live),
				"the status text must be seeded before aria-live registers the region,"
				+ " otherwise the initial render is announced as a slide change — got: " + seq);
	}

	@Test
	public void aria_roledescription_author_supplied_via_ca_is_preserved() {
		// ca:aria-roledescription is the only handle an app has on the announced
		// widget type, on the carousel and on each slide alike.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("custom-carousel", jq("$cr-ca").attr("aria-roledescription"),
				"author-supplied ca:aria-roledescription must survive on the carousel");
		assertEquals("custom-slide", jq("$ci-rd").attr("aria-roledescription"),
				"author-supplied ca:aria-roledescription must survive on the slide");
		assertEquals("carousel", jq("$cr1").attr("aria-roledescription"),
				"a carousel with no ca: value still gets the default roledescription");
		assertEquals("slide", jq("$ci0").attr("aria-roledescription"),
				"a slide with no ca: value still gets the default roledescription");
	}

	@Test
	public void aria_roledescription_defaults_come_from_the_msgza11y_bundle() {
		// aria-roledescription is spoken prose, so it has to be translatable —
		// a literal in the augment can never be localized or overridden.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		// getEval stringifies, so a key missing from msgza11y comes back as "null".
		String carousel = getEval("msgza11y.CAROUSEL_ROLEDESC"),
				slide = getEval("msgza11y.CAROUSELITEM_ROLEDESC");
		assertNotEquals("null", carousel, "msgza11y must define CAROUSEL_ROLEDESC");
		assertNotEquals("null", slide, "msgza11y must define CAROUSELITEM_ROLEDESC");
		assertEquals(carousel, jq("$cr1").attr("aria-roledescription"),
				"the carousel roledescription must be the bundle value");
		assertEquals(slide, jq("$ci0").attr("aria-roledescription"),
				"the slide roledescription must be the bundle value");
		// The shipped values equal the literals they replaced, so mutate the bundle
		// and rebind — only an augment that CONSULTS it follows.
		getEval("(msgza11y.CAROUSEL_ROLEDESC='ZZ-carousel',"
				+ "msgza11y.CAROUSELITEM_ROLEDESC='ZZ-slide',"
				+ "zk.Widget.$(jq('$cr1')[0]).rerender(-1),'')");
		assertEquals("ZZ-carousel", jq("$cr1").attr("aria-roledescription"),
				"the carousel roledescription must come from msgza11y, not a literal");
		assertEquals("ZZ-slide", jq("$ci0").attr("aria-roledescription"),
				"the slide roledescription must come from msgza11y, not a literal");
	}

	@Test
	public void carouselitem_aria_label_author_supplied_via_ca_is_preserved() {
		// The parent already promises ca:aria-label survives; the child must not
		// fold the caption + position over it.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("Autumn collection", jq("$ci-ca").attr("aria-label"),
				"author-supplied ca:aria-label must survive on the slide");
	}

	@Test
	public void carouselitem_aria_label_still_tracks_setLabel_when_author_supplies_none() {
		// The guard must key off the author's ca: value, not off "an aria-label
		// is already present" — the latter would freeze the name bind_ wrote.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("Card C (Slide 3 of 3)", jq("$ciPlain").attr("aria-label"),
				"a slide with no ca:aria-label keeps the caption+position default");
		click(jq("$btn-relabel-slide"));
		waitResponse();
		assertEquals("Card Z (Slide 3 of 3)", jq("$ciPlain").attr("aria-label"),
				"setLabel must still re-derive the aria-label it owns");
	}

	@Test
	public void aria_label_not_injected_when_author_supplies_none() {
		// 6-11: no generic default name is injected (it would duplicate the
		// aria-roledescription="carousel"); naming is the author's via ca:.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		// jq().attr() returns the string "null" for an absent attribute, so query
		// the DOM directly: the region must carry no aria-label of its own.
		assertEquals("false", getEval("jq('$cr1')[0].hasAttribute('aria-label')"),
				"za11y must not inject a generic region name onto a carousel that supplies none");
	}
}
