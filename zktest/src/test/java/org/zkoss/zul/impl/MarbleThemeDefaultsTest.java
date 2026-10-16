/* MarbleThemeDefaultsTest.java

	Purpose:

	Description:

	History:
		Mon Oct 05 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.zkoss.lang.Library;

/**
 * Unit tests for the zk.xml Marble defaults of {@link PageRenderer}: parsing the
 * org.zkoss.theme.marble.density / org.zkoss.theme.marble.brand library properties, and
 * rendering them as root attributes and a primary-seed style (ZK-6112).
 */
public class MarbleThemeDefaultsTest {
	private static final String DENSITY = "org.zkoss.theme.marble.density";
	private static final String BRAND = "org.zkoss.theme.marble.brand";

	@AfterEach
	public void resetProperties() {
		Library.setProperty(DENSITY, null);
		Library.setProperty(BRAND, null);
	}

	// ---- parseThemeDefaults ----

	@Test
	public void parseNothingConfigured() {
		assertParsed(null, null, null);
	}

	@Test
	public void parseCompactDensity() {
		Library.setProperty(DENSITY, "compact");
		assertParsed("compact", null, null);
	}

	@Test
	public void parseDensityIgnoresCaseAndWhitespace() {
		Library.setProperty(DENSITY, " COMPACT ");
		assertParsed("compact", null, null);
	}

	@Test
	public void parseComfortableDensityRendersNothing() {
		Library.setProperty(DENSITY, "comfortable");
		assertParsed(null, null, null);
	}

	@Test
	public void parseUnknownDensityIsIgnored() {
		Library.setProperty(DENSITY, "tiny");
		assertParsed(null, null, null);
	}

	@Test
	public void parseBrandPreset() {
		Library.setProperty(BRAND, "slate");
		assertParsed(null, "slate", null);
		Library.setProperty(BRAND, "Copper");
		assertParsed(null, "copper", null);
	}

	@Test
	public void parseDefaultBrandRendersNothing() {
		Library.setProperty(BRAND, "default");
		assertParsed(null, null, null);
	}

	@Test
	public void parseHexBrand() {
		Library.setProperty(BRAND, "#0a7d5a");
		assertParsed(null, null, "#0a7d5a");
		Library.setProperty(BRAND, "#ABC");
		assertParsed(null, null, "#ABC");
	}

	@Test
	public void parseInvalidBrandIsIgnored() {
		for (String value : new String[] {"red", "#12345", "rgb(1,2,3)", "#0a7d5a;}body{x:y"}) {
			Library.setProperty(BRAND, value);
			assertParsed(null, null, null);
		}
	}

	@Test
	public void parseInvalidDensityDoesNotAffectBrand() {
		Library.setProperty(DENSITY, "tiny");
		Library.setProperty(BRAND, "slate");
		assertParsed(null, "slate", null);
	}

	// ---- outThemeRootAttributes ----

	@Test
	public void rootAttributesNothingConfigured() {
		assertNull(PageRenderer.outThemeRootAttributes(defaults(null, null, null), ""));
	}

	@Test
	public void rootAttributesDensityAndBrand() {
		assertEquals(" data-density=\"compact\" data-brand=\"slate\"",
				PageRenderer.outThemeRootAttributes(defaults("compact", "slate", null), ""));
	}

	@Test
	public void rootAttributesPageAttributeWins() {
		assertEquals(" data-brand=\"slate\"",
				PageRenderer.outThemeRootAttributes(defaults("compact", "slate", null), "data-density=\"comfortable\""));
	}

	@Test
	public void rootAttributesPageAttributeMatchIgnoresCaseAndSpaces() {
		assertNull(PageRenderer.outThemeRootAttributes(defaults("compact", null, null), "DATA-DENSITY = \"x\""));
	}

	@Test
	public void rootAttributesSimilarPageAttributeDoesNotWin() {
		assertEquals(" data-density=\"compact\"",
				PageRenderer.outThemeRootAttributes(defaults("compact", null, null), "data-density-x=\"1\""));
	}

	@Test
	public void rootAttributesNullPageAttributes() {
		assertEquals(" data-density=\"compact\"",
				PageRenderer.outThemeRootAttributes(defaults("compact", null, null), null));
	}

	// ---- outThemeStyle ----

	@Test
	public void styleNothingConfigured() {
		assertNull(PageRenderer.outThemeStyle(defaults(null, "slate", null)));
	}

	@Test
	public void stylePrimaryColor() {
		assertEquals("<style>:root{--zk-color-primary:#0a7d5a}</style>\n",
				PageRenderer.outThemeStyle(defaults(null, null, "#0a7d5a")));
	}

	private static PageRenderer.MarbleThemeDefaults defaults(String density, String brand, String primaryColor) {
		return new PageRenderer.MarbleThemeDefaults(density, brand, primaryColor);
	}

	private static void assertParsed(String density, String brand, String primaryColor) {
		PageRenderer.MarbleThemeDefaults defaults = PageRenderer.parseThemeDefaults();
		assertEquals(density, defaults.density, "density");
		assertEquals(brand, defaults.brand, "brand");
		assertEquals(primaryColor, defaults.primaryColor, "primaryColor");
	}
}
