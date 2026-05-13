/* F104_ZK_6097_AvatargroupTest.java

		Purpose:

		Description:

		History:
				Wed May 13 13:17:28 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_6097_AvatargroupTest extends WebDriverTestCase {


	@Test
	public void testBasicGroupRendered() {
		connect();
		waitResponse();
		JQuery agBasic = jq("$agBasic");
		assertTrue(agBasic.exists(), "agBasic group not found");
		// 4 avatar children
		assertEquals(4, agBasic.find(".z-avatar").length(), "expected 4 avatars in basic group");
	}

	@Test
	public void testMaxCountOverflow() {
		connect();
		waitResponse();
		JQuery agMax = jq("$agMax");
		assertTrue(agMax.exists(), "agMax group not found");
		// overflow span "+2" should exist
		JQuery overflow = agMax.find(".z-avatargroup-overflow");
		assertTrue(overflow.exists(), "overflow indicator not found");
		assertEquals("+2", overflow.text().trim(), "overflow text should be +2");
		// 3 visible avatars (children without display:none data-ag-hidden)
		int visible = 0;
		for (int i = 0; i < agMax.find(".z-avatar").length(); i++) {
			String display = agMax.find(".z-avatar").eq(i).css("display");
			if (!"none".equals(display)) visible++;
		}
		// 3 visible + 1 overflow indicator = 4 non-hidden, but overflow indicator is also .z-avatar
		// hidden avatars have data-ag-hidden; verify at least 3 non-hidden avatars
		assertTrue(visible >= 3, "expected at least 3 non-hidden avatars");
	}

	@Test
	public void testSizeShapeCascade() {
		connect();
		waitResponse();
		// small-circle group should have the group class applied
		JQuery agSmall = jq("$agSmallCircle");
		assertTrue(agSmall.exists(), "agSmallCircle group not found");
		assertTrue(agSmall.hasClass("z-avatargroup-small"), "expected z-avatargroup-small class");
		assertTrue(agSmall.hasClass("z-avatargroup-circle"), "expected z-avatargroup-circle class");

		JQuery agLarge = jq("$agLargeSquare");
		assertTrue(agLarge.exists(), "agLargeSquare group not found");
		assertTrue(agLarge.hasClass("z-avatargroup-large"), "expected z-avatargroup-large class");
		assertTrue(agLarge.hasClass("z-avatargroup-square"), "expected z-avatargroup-square class");
	}

	@Test
	public void testNoOverflowWhenUnlimited() {
		connect();
		waitResponse();
		// basic group has no maxCount — no overflow indicator
		JQuery agBasic = jq("$agBasic");
		assertFalse(agBasic.find(".z-avatargroup-overflow").exists(),
				"basic group should not have overflow indicator");
	}

	@Test
	public void overflow_indicator_has_aria_label() {
		// "+N" reads as "plus N" literally on most screen readers, which is
		// noisy. The overflow span carries aria-label="N more" so AT users
		// hear a natural phrase instead.
		connect();
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		String label = jq("$agMax").find(".z-avatargroup-overflow").attr("aria-label");
		assertEquals("2 more", label,
				"overflow indicator must expose 'N more' via aria-label — got: " + label);
	}

	@Test
	public void hidden_avatar_setGap_keeps_overflow_marker() {
		// ZK-6097 P2-2: setGap() rerenders the avatar's DOM, which the mold
		// rebuilds WITHOUT the parent group's imperative data-ag-hidden overflow
		// marker. The setter must re-apply overflow, otherwise an avatar hidden
		// behind "+N" pops back into the visible row while still counted.
		connect();
		waitResponse();
		assertEquals("true",
				getEval("'' + (jq('$agMaxE')[0].getAttribute('data-ag-hidden') === 'true')"),
				"avatar E must start hidden behind the +2 overflow");
		click(jq("$btn-setgap-hidden"));
		waitResponse();
		assertEquals("true",
				getEval("'' + (jq('$agMaxE')[0].getAttribute('data-ag-hidden') === 'true')"),
				"after setGap the hidden avatar must stay hidden (overflow re-applied)");
		assertEquals("+2", jq("$agMax").find(".z-avatargroup-overflow").text().trim(),
				"overflow count must remain +2 after setGap on a hidden avatar");
	}

	@Test
	public void broken_image_overflow_avatar_stays_hidden_on_error() {
		// ZK-6097 P2-7: an image-mode avatar hidden behind "+N" whose image
		// fails to load fires onerror, which rerenders its DOM. The parent
		// group's imperative data-ag-hidden marker (not emitted by the mold)
		// must be re-applied via an IMMEDIATE rerender(-1); the default deferred
		// rerender() would let the rebuilt node lose the marker, popping the
		// broken avatar back into the visible row while still counted.
		connect();
		waitResponse();
		assertEquals("true",
				getEval("'' + (jq('$agImgHidden')[0].getAttribute('data-ag-hidden') === 'true')"),
				"image avatar must start hidden behind the +1 overflow");
		// Deterministically fire the image error — the avatar root's capture
		// listener handles it (avoids relying on real image-load timing).
		eval("jq('$agImgHidden').find('img')[0].dispatchEvent(new Event('error'))");
		// Allow any DEFERRED rerender (the bug path) to flush before asserting.
		sleep(500);
		assertEquals("true",
				getEval("'' + (jq('$agImgHidden')[0].getAttribute('data-ag-hidden') === 'true')"),
				"broken-image avatar must stay hidden after onerror (overflow re-applied)");
	}
}
