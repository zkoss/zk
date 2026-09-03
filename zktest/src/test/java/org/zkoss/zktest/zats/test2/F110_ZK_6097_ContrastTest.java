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

	@Test
	public void overriding_the_accent_token_restyles_badge_on_every_severity() {
		connect();
		waitResponse();
		assertAll(java.util.Arrays.stream(SEVERITIES).map(s -> () -> {
			overrideToken("--zk-severity-" + s + "-accent-color", PROBE);
			assertEquals(PROBE, computed("jq('$bg-" + s + "').find('.z-badge-indicator')[0]", "background-color"),
					"badge " + s + " ignored the :root override, so a _wcag* palette cannot restyle it");
		}));
	}

	@Test
	public void overriding_the_text_token_restyles_chip_on_every_severity() {
		connect();
		waitResponse();
		assertAll(java.util.Arrays.stream(SEVERITIES).map(s -> () -> {
			overrideToken("--zk-severity-" + s + "-text-color", PROBE);
			assertEquals(PROBE, computed("jq('$cp-" + s + "')[0]", "color"),
					"chip " + s + " ignored the :root override, so a _wcag* palette cannot restyle it");
		}));
	}

	@Test
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
		}
		assertAll(java.util.stream.IntStream.range(0, SEVERITIES.length).mapToObj(i -> () -> assertEquals(
				PROBE, measured[i],
				"confirmpopup " + SEVERITIES[i] + " ignored the :root override")));
	}

	// ----- the documented gap is still the gap -----

	@Test
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

	private void overrideToken(String name, String value) {
		getEval("(function () {"
				+ " document.documentElement.style.setProperty('" + name + "', '" + value + "');"
				+ " return '';"
				+ "})()");
	}
}
