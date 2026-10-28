/* F110_ZK_6097_ContrastTest.java

	Purpose:

	Description:

	History:
		Mon Aug 31 17:40:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Pins the token indirection a {@code _wcag*} palette needs, not the hex values.
 * Asserted by <em>moving</em> the token: comparing computed colours passes just as
 * happily against a consumer hardcoded to the same literal.
 */
public class F110_ZK_6097_ContrastTest extends WebDriverTestCase {

	private static final String[] SEVERITIES = { "info", "success", "warning", "error", "neutral" };

	/** A colour no palette would ever pick, so a stale literal cannot coincide with it. */
	private static final String PROBE = "rgb(1, 2, 3)";

	// ----- a theme override reaches every consumer of every severity -----

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void overriding_the_accent_token_restyles_badge_on_every_severity() {
		connect();
		waitResponse();
		assertAll(java.util.Arrays.stream(SEVERITIES).map(s -> () -> {
			overrideToken("--zk-severity-" + s + "-accent-color", PROBE);
			assertEquals(PROBE, computed("jq('$bg-" + s + "').find('.z-badge-indicator')[0]", "background-color"),
					"badge " + s + " ignored the :root override, so a _wcag* palette cannot restyle it");
		}));
	}

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void overriding_the_text_token_restyles_chip_on_every_severity() {
		connect();
		waitResponse();
		assertAll(java.util.Arrays.stream(SEVERITIES).map(s -> () -> {
			overrideToken("--zk-severity-" + s + "-text-color", PROBE);
			assertEquals(PROBE, computed("jq('$cp-" + s + "')[0]", "color"),
					"chip " + s + " ignored the :root override, so a _wcag* palette cannot restyle it");
		}));
	}

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void overriding_the_accent_token_restyles_confirmpopup_on_every_severity() {
		connect();
		waitResponse();
		String[] measured = new String[SEVERITIES.length];
		for (int i = 0; i < SEVERITIES.length; i++) {
			overrideToken("--zk-severity-" + SEVERITIES[i] + "-accent-color", PROBE);
			click(jq("$btn-" + SEVERITIES[i]));
			waitResponse();
			String id = "p" + Character.toUpperCase(SEVERITIES[i].charAt(0)) + SEVERITIES[i].substring(1);
			measured[i] = computed("jq('$" + id + "').find('.z-confirmpopup-icon')[0]", "color");
			// ZK-6164: an open popup can sit over the next trigger, so close it first
			click(jq("$" + id).find(".z-confirmpopup-cancel"));
			waitResponse();
		}
		assertAll(java.util.stream.IntStream.range(0, SEVERITIES.length).mapToObj(i -> () -> assertEquals(
				PROBE, measured[i],
				"confirmpopup " + SEVERITIES[i] + " ignored the :root override")));
	}

	// ----- the single-declaration knobs, each read back off its own node -----

	// One token, one consumer, no second declaration to fall back on: revert the
	// consumer and only a probe override catches it. A computed-colour comparison
	// would not — it passes just as happily against the hardcoded literal.

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void overriding_the_avatar_token_restyles_the_label() {
		connect();
		waitResponse();
		overrideToken("--zk-avatar-color", PROBE);
		assertEquals(PROBE, computed("jq('$ct-avatar')[0]", "color"),
				"avatar ignored the :root override, so a _wcag* palette cannot restyle it");
	}

	// IceBlue-only: it parses the computed colours as rgb(); Marble's avatar colours compute to oklch().
	@Test
	@Tag("IceBlueOnly")
	public void default_avatar_foreground_has_sufficient_contrast() {
		connect();
		waitResponse();
		for (String id : new String[] { "ct-avatar-small", "ct-avatar", "ct-avatar-large", "ct-avatar-icon" }) {
			String avatar = "jq('$" + id + "')[0]";
			String glyph = avatar + (id.equals("ct-avatar-icon") ? ".querySelector('i')"
					: ".querySelector('.z-avatar-text')");
			assertEquals("true", getEval("(function (n) {"
					+ " n.scrollIntoView({block: 'center'});"
					+ " var r = n.getBoundingClientRect();"
					+ " var t = document.elementsFromPoint(r.left + r.width / 2, r.top + r.height / 2)[0];"
					+ " return !!t && (n === t || n.contains(t));"
					+ "})(" + avatar + ")"), id + " must be painted at its centre");
			double ratio = contrastRatio(computed(glyph, "color"), computed(avatar, "background-color"));
			assertTrue(ratio >= 4.5, id + " foreground contrast must be at least 4.5:1, was " + ratio + ":1");
		}
	}

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void overriding_the_breadcrumb_tokens_restyles_separator_and_ellipsis() {
		connect();
		waitResponse();
		overrideToken("--zk-breadcrumb-separator-color", PROBE);
		overrideToken("--zk-breadcrumb-ellipsis-color", PROBE);
		assertAll(
				() -> assertEquals(PROBE,
						computed("jq('$ct-bc').find('.z-breadcrumb-separator')[0]", "color"),
						"breadcrumb separator ignored the :root override"),
				// maxItems="3" against 4 items is what injects the ellipsis node.
				() -> assertEquals(PROBE,
						computed("jq('$ct-bc').find('.z-breadcrumb-ellipsis')[0]", "color"),
						"breadcrumb ellipsis ignored the :root override"));
	}

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void overriding_the_chip_close_token_restyles_its_opacity() {
		connect();
		waitResponse();
		// 0.5 is not the shipped default, so a stale literal cannot coincide.
		overrideToken("--zk-chip-close-opacity", "0.5");
		assertEquals("0.5", computed("jq('$ct-chip').find('.z-chip-close')[0]", "opacity"),
				"chip close ignored the :root override, so a _wcag* palette cannot restyle it");
	}

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void overriding_the_box_shadow_tokens_restyles_badge_and_avatargroup() {
		connect();
		waitResponse();
		String probeShadow = "rgb(1, 2, 3) 0px 0px 0px 4px";
		overrideToken("--zk-badge-indicator-box-shadow", probeShadow);
		overrideToken("--zk-avatargroup-overflow-box-shadow", probeShadow);
		assertAll(
				() -> assertEquals(probeShadow,
						computed("jq('$bg-info').find('.z-badge-indicator')[0]", "box-shadow"),
						"badge indicator ignored the :root override"),
				// maxItems="2" against 3 avatars is what injects the "+N" node.
				() -> assertEquals(probeShadow,
						computed("jq('$ct-ag').find('.z-avatargroup-overflow')[0]", "box-shadow"),
						"avatargroup overflow ignored the :root override"));
	}

	// ----- the documented gap is still the gap -----

	// IceBlue-only: depends on IceBlue's --zk-severity-* / WCAG-palette token names (and the Ant palette hex), which Marble does not define.
	@Test
	@Tag("IceBlueOnly")
	public void ce_defaults_are_the_ant_palette_not_an_accessible_one() {
		// Guards the decision in profiles/_default.less: darkening the CE defaults
		// must be a deliberate revisit, not a silent change to every app's look.
		connect();
		waitResponse();
		assertAll(
				() -> assertEquals("rgb(22, 119, 255)", token("--zk-severity-info-accent-color")),
				() -> assertEquals("rgb(82, 196, 26)", token("--zk-severity-success-accent-color")),
				() -> assertEquals("rgb(250, 173, 20)", token("--zk-severity-warning-accent-color")),
				() -> assertEquals("rgb(255, 77, 79)", token("--zk-severity-error-accent-color")),
				() -> assertEquals("rgb(140, 140, 140)", token("--zk-severity-neutral-accent-color")));
	}

	@Test
	public void badge_dot_renders_no_text_so_only_its_fill_carries_it() {
		// Why the accent doubles as a non-text colour, and therefore why a theme
		// replacing it has to satisfy SC 1.4.11 as well as SC 1.4.3.
		connect();
		waitResponse();
		assertTrue(getEval("jq('$bg-dot').find('.z-badge-indicator')[0].textContent").isEmpty(),
				"the dot variant is expected to render no text");
	}

	/** Computed value of a {@code :root} custom property, normalised to {@code rgb(...)}. */
	private String token(String name) {
		return getEval("(function () {"
				+ " var v = getComputedStyle(document.documentElement).getPropertyValue('" + name + "').trim(),"
				+ "     p = document.createElement('span');"
				+ " p.style.color = v;"
				+ " document.body.appendChild(p);"
				+ " var out = getComputedStyle(p).color;"
				+ " p.remove();"
				+ " return out;"
				+ "})()");
	}

	private String computed(String elementJs, String prop) {
		return getEval("getComputedStyle(" + elementJs + ").getPropertyValue('" + prop + "')");
	}

	private static double contrastRatio(String foreground, String background) {
		double[] fg = rgba(foreground);
		double[] bg = rgba(background);
		assertEquals(1.0, bg[3], "the avatar contrast fixture must have an opaque background");
		for (int i = 0; i < 3; i++) {
			fg[i] = fg[i] * fg[3] + bg[i] * (1 - fg[3]);
		}
		double fgLuminance = luminance(fg);
		double bgLuminance = luminance(bg);
		return (Math.max(fgLuminance, bgLuminance) + 0.05) / (Math.min(fgLuminance, bgLuminance) + 0.05);
	}

	private static double[] rgba(String color) {
		String[] channels = color.substring(color.indexOf('(') + 1, color.indexOf(')')).split(",");
		return new double[] { Double.parseDouble(channels[0].trim()), Double.parseDouble(channels[1].trim()),
				Double.parseDouble(channels[2].trim()), channels.length == 4 ? Double.parseDouble(channels[3].trim()) : 1 };
	}

	private static double luminance(double[] color) {
		double[] linear = new double[3];
		for (int i = 0; i < 3; i++) {
			double channel = color[i] / 255;
			linear[i] = channel <= 0.04045 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
		}
		return 0.2126 * linear[0] + 0.7152 * linear[1] + 0.0722 * linear[2];
	}

	private void overrideToken(String name, String value) {
		getEval("(function () {"
				+ " document.documentElement.style.setProperty('" + name + "', '" + value + "');"
				+ " return '';"
				+ "})()");
	}
}
