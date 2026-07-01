/* F104_ZK_6099Test.java

        Purpose:
                
        Description:
                
        History:
                Mon May 18 16:37:08 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6099Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		// Server-side getter round-trip across multiple component types.
		click(jq("$readBack"));
		waitResponse();
		assertEquals("rect|circle|text|rect", jq("$probe").val());

		// Clearing the attribute returns null.
		click(jq("$clear"));
		click(jq("$readBack"));
		waitResponse();
		assertEquals("null|circle|text|rect", jq("$probe").val());

		// Invalid value throws WrongValueException.
		click(jq("$invalid"));
		waitResponse();
		assertTrue(jq("$probe").val().startsWith("threw:"),
				"setSkeleton(\"bogus\") must throw, got: " + jq("$probe").val());
	}

	/** Enum guard: the setter is case-sensitive — "Rect" (capital R) must raise
	 *  WrongValueException, same as any other non-enum value. Mirrors how peer
	 *  ZK string-enum setters (orient, mode, ...) behave.
	 */
	@Test
	public void testEnumGuardRejectsWrongCase() {
		connect();
		waitResponse();

		click(jq("$setCase"));
		waitResponse();
		assertTrue(jq("$probe").val().startsWith("threw:"),
				"setSkeleton(\"Rect\") must throw (case-sensitive); got: " + jq("$probe").val());
	}

	/** Empty string is normalized to "off" (null), not rejected — consistent with
	 *  every other HtmlBasedComponent string setter (sclass, tooltiptext, style, ...)
	 *  and with the common data-binding case where {@code skeleton="@load(vm.mode)"}
	 *  resolves to "". The probe reads back {@code getSkeleton()}, which must be null.
	 */
	@Test
	public void testEmptyStringClearsSkeleton() {
		connect();
		waitResponse();

		click(jq("$setEmpty"));
		waitResponse();
		assertEquals("null", jq("$probe").val(),
				"setSkeleton(\"\") should clear skeleton (normalize to null), not throw; got: "
						+ jq("$probe").val());
	}

	/** Client mixin (zkex) contract: when skeleton is set the widget's root
	 *  carries the active class, the mode-specific class, and aria-busy.
	 *  Clearing the value removes both classes and the attribute.
	 */
	@Test
	public void testClientDomReflectsSkeleton() {
		connect();
		waitResponse();

		assertTrue(jq("$g1").hasClass("z-skeleton-active"),
				"grid should have z-skeleton-active");
		assertTrue(jq("$g1").hasClass("z-skeleton-rect"),
				"grid should have z-skeleton-rect");
		assertEquals("true", jq("$g1").attr("aria-busy"));

		assertTrue(jq("$i1").hasClass("z-skeleton-circle"),
				"image should have z-skeleton-circle");
		assertTrue(jq("$l1").hasClass("z-skeleton-text"),
				"label should have z-skeleton-text");

		// The class alone does not prove the content is hidden. A label's text is
		// a direct text node, so it must be masked by the ::after overlay: assert
		// the overlay actually generates a box (content != none/normal).
		String l1After = getEval("window.getComputedStyle(jq('$l1')[0], '::after').content");
		assertTrue(l1After != null && !"none".equals(l1After) && !"normal".equals(l1After),
				"label skeleton must paint an ::after overlay over its text; got: " + l1After);
		// An image is a replaced <img>: it cannot host an ::after overlay, and its
		// bitmap is not hidable by portable CSS. So the mixin hides the <img>'s own
		// content (z-skeleton-masked -> visibility:hidden) and lays a separate
		// overlay div over it. Assert both — a regression that merely re-adds the
		// class without masking, or drops the overlay, is then caught.
		assertTrue(jq("$i1").hasClass("z-skeleton-masked"),
				"image's own bitmap must be hidden via z-skeleton-masked");
		assertEquals("hidden", getEval("window.getComputedStyle(jq('$i1')[0]).visibility"),
				"image bitmap must be visibility:hidden so it can't bleed through the overlay");
		assertEquals("1", getEval("jq('$i1').parent().find('.z-skeleton-overlay').length"),
				"image skeleton must lay exactly one overlay div over the <img>");

		// Clearing a replaced root tears its overlay back down (removeSkeletonOverlay).
		eval("zk.Widget.$(jq('$i1')[0]).setSkeleton(null)");
		assertEquals("0", getEval("jq('$i1').parent().find('.z-skeleton-overlay').length"),
				"clearing skeleton must remove the image overlay");
		assertTrue(!jq("$i1").hasClass("z-skeleton-masked"),
				"clearing skeleton must drop z-skeleton-masked");

		click(jq("$clear"));
		waitResponse();
		assertTrue(!jq("$g1").hasClass("z-skeleton-active"),
				"clearing skeleton should remove z-skeleton-active");
		assertTrue(!jq("$g1").hasClass("z-skeleton-rect"),
				"clearing skeleton should remove z-skeleton-rect");
		assertEquals("null", jq("$g1").attr("aria-busy"));
	}

	/** Client mixin must handle the post-bind dynamic path: a widget that was
	 *  bound without skeleton and later receives {@code setSkeleton('rect')} via
	 *  smartUpdate must end up with the CSS classes and aria-busy applied. This
	 *  exercises the dynamic {@code setSkeleton} -&gt; {@code applySkeletonDom} path,
	 *  which the static-attribute path in
	 *  {@link #testClientDomReflectsSkeleton} does NOT cover.
	 */
	@Test
	public void testDynamicSetSkeletonAddsClasses() {
		connect();
		waitResponse();

		// Initially g2 has no skeleton — confirm the page is set up that way.
		assertTrue(!jq("$g2").hasClass("z-skeleton-active"),
				"g2 should start without z-skeleton-active");

		click(jq("$setDynamic"));
		waitResponse();

		assertTrue(jq("$g2").hasClass("z-skeleton-active"),
				"dynamic setSkeleton should add z-skeleton-active");
		assertTrue(jq("$g2").hasClass("z-skeleton-rect"),
				"dynamic setSkeleton should add z-skeleton-rect");
		assertEquals("true", jq("$g2").attr("aria-busy"));
	}

	/** Ownership flag: when aria-busy is set by another subsystem before
	 *  skeleton is toggled, a subsequent setSkeleton(...) → setSkeleton(null)
	 *  cycle must NOT strip the foreign attribute. Guards against a regression
	 *  back to unconditional {@code removeAttribute('aria-busy')}.
	 */
	@Test
	public void testAriaBusyOwnershipPreservesForeignAttribute() {
		connect();
		waitResponse();

		// Pre-claim aria-busy on l2 from client-side, before any skeleton activity.
		eval("jq('$l2').attr('aria-busy', 'true')");
		assertEquals("true", jq("$l2").attr("aria-busy"));

		// Drive set + clear as TWO separate client transitions. A single server
		// event (one button click calling setSkeleton("text") then setSkeleton(null))
		// would smartUpdate-coalesce both into one final skeleton=null; the client
		// would see _skeleton already undefined and early-return, so applySkeletonDom
		// — and the ownership branch this test guards — would never run.
		eval("zk.Widget.$(jq('$l2')[0]).setSkeleton('text')");
		eval("zk.Widget.$(jq('$l2')[0]).setSkeleton(null)");

		// The mixin saw aria-busy already present, so it never claimed ownership;
		// the clear must therefore leave the foreign attribute intact.
		assertEquals("true", jq("$l2").attr("aria-busy"),
				"foreign aria-busy should survive a setSkeleton/clear cycle");
	}

	/** F-1 regression (zkex {@code SKELETON_OVERLAY_TAGS}): form-control and
	 *  replaced/media roots — a multiline Textbox ({@code <textarea>}), a Selectbox
	 *  ({@code <select>}) and an Audio ({@code <audio>}) — cannot host an ::after
	 *  mask, so the mixin must hide their own content ({@code z-skeleton-masked} ->
	 *  visibility:hidden) and lay a real overlay div over them, exactly as it does
	 *  for the {@code <img>} in {@link #testClientDomReflectsSkeleton}. A regression
	 *  that drops any of these tags from the overlay allowlist leaves the control's
	 *  real content showing through the (non-rendering) ::after — caught here. Also
	 *  covers the server getter round-trip and the overlay teardown on clear.
	 */
	@Test
	public void testOverlayPathFormControlAndMediaRoots() {
		connect();
		waitResponse();

		// Server getSkeleton round-trip across the three new root types.
		click(jq("$readBackOverlay"));
		waitResponse();
		assertEquals("rect|circle|text", jq("$probe").val(),
				"server getSkeleton round-trip for textarea|select|audio roots");

		// {id, the mode each was given in the ZUL}.
		String[][] roots = {{"t1", "rect"}, {"s1", "circle"}, {"a1", "text"}};
		for (String[] r : roots) {
			String sel = "$" + r[0], mode = r[1];
			assertTrue(jq(sel).hasClass("z-skeleton-active"),
					r[0] + " must have z-skeleton-active");
			assertTrue(jq(sel).hasClass("z-skeleton-" + mode),
					r[0] + " must have z-skeleton-" + mode);
			// These roots render no ::after, so they must take the replaced-root path:
			// own content masked + a separate overlay div, NOT an in-place ::after.
			assertTrue(jq(sel).hasClass("z-skeleton-masked"),
					r[0] + " (form-control/replaced root) must be hidden via z-skeleton-masked");
			assertEquals("hidden", getEval("window.getComputedStyle(jq('" + sel + "')[0]).visibility"),
					r[0] + " content must be visibility:hidden so it cannot bleed through the overlay");
			assertEquals("1", getEval("jq('" + sel + "').parent().find('.z-skeleton-overlay').length"),
					r[0] + " must lay exactly one overlay div over its root");
			assertEquals("true", jq(sel).attr("aria-busy"), r[0] + " must be aria-busy");
		}

		// Toggle off the textarea: overlay torn down, masked/active dropped.
		eval("zk.Widget.$(jq('$t1')[0]).setSkeleton(null)");
		assertEquals("0", getEval("jq('$t1').parent().find('.z-skeleton-overlay').length"),
				"clearing skeleton must remove the textarea overlay");
		assertTrue(!jq("$t1").hasClass("z-skeleton-masked"),
				"clearing skeleton must drop z-skeleton-masked on the textarea");
		assertTrue(!jq("$t1").hasClass("z-skeleton-active"),
				"clearing skeleton must drop z-skeleton-active on the textarea");
	}

	/** Overlay geometry + lifecycle, asserted POSITIVELY (covers the overlay
	 *  resize/rebuild concern family — the accepted IG-7/IG-9/IG-11 trade-offs —
	 *  by verifying the desired contract instead of locking in a limitation):
	 *  <ol>
	 *  <li>the JS-laid overlay is sized AND positioned to the replaced root's
	 *      offset box (so a borderless host lines up exactly);</li>
	 *  <li>a full server rerender ({@code invalidate()} -&gt; {@code unbind_}/
	 *      {@code bind_}) rebuilds the overlay cleanly — exactly one, no
	 *      orphan/duplicate — and re-applies {@code z-skeleton-masked};</li>
	 *  <li>after the rebind the overlay re-aligns to the root's offset box again
	 *      (the documented "re-aligns on next bind" behaviour).</li>
	 *  </ol>
	 *  The skipper-skip orphan (IG-9) and bordered-host sub-pixel offset (IG-11)
	 *  remain accepted limitations and are deliberately NOT asserted here.
	 */
	@Test
	public void testOverlayGeometryAndCleanRebuildOnRerender() {
		connect();
		waitResponse();

		// (1) Overlay sized/positioned to the <img>'s offset box on first render.
		assertOverlaySizedToRoot("$i1", "initial render");

		// (2)+(3) Full rerender: unbind_ tears the overlay down, bind_ rebuilds it.
		click(jq("$invalidateImg"));
		waitResponse();
		assertTrue(jq("$i1").hasClass("z-skeleton-masked"),
				"a full rerender must re-apply z-skeleton-masked on the <img>");
		assertOverlaySizedToRoot("$i1", "after invalidate/rebind");
	}

	/** Asserts the replaced-root overlay exists exactly once under the root's
	 *  parent and that its inline geometry matches the root's offset box. Returns
	 *  a descriptive mismatch (not just false) so a failure pinpoints the axis.
	 */
	private void assertOverlaySizedToRoot(String rootSel, String when) {
		String js = "(function(){"
				+ "var n=jq('" + rootSel + "')[0];"
				+ "var ovs=jq('" + rootSel + "').parent().find('.z-skeleton-overlay');"
				+ "if(ovs.length!==1) return 'overlay count='+ovs.length;"
				+ "var o=ovs[0];"
				+ "if(o.style.width!==n.offsetWidth+'px') return 'width '+o.style.width+'!='+n.offsetWidth+'px';"
				+ "if(o.style.height!==n.offsetHeight+'px') return 'height '+o.style.height+'!='+n.offsetHeight+'px';"
				+ "if(o.style.left!==n.offsetLeft+'px') return 'left '+o.style.left+'!='+n.offsetLeft+'px';"
				+ "if(o.style.top!==n.offsetTop+'px') return 'top '+o.style.top+'!='+n.offsetTop+'px';"
				+ "return 'ok';})()";
		assertEquals("ok", getEval(js),
				"overlay must be sized/positioned to the root's offset box (" + when + ")");
	}
}
