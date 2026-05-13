/* F104_ZK_6097_CarouselTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:08:11 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_CarouselTest extends WebDriverTestCase {

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
		assertTrue(err.contains("activeIndex cannot be negative"),
				"negative activeIndex must throw — got: " + err);
		assertTrue(jq("$ci0").hasClass("z-carouselitem-active"),
				"failed setActiveIndex must not mutate active slide");
	}

	@Test
	public void activeIndex_above_child_count_throws() {
		connect();
		waitResponse();
		click(jq("$btn-overflow-active"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("activeIndex must be less than"),
				"activeIndex >= child count must throw — got: " + err);
		assertTrue(jq("$ci0").hasClass("z-carouselitem-active"),
				"overflow setActiveIndex must not mutate active slide");
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
