/* F104_ZK_5409_ValidationTest.java

	Purpose:

	Description:

	History:
		Tue May 12 10:22:52 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.zk.ui.WrongValueException;

/**
 * Server-side validation tests for ZK-5409 setters. Pure JUnit — no WebDriver,
 * no Jetty, no ZATS environment. Verifies the {@link WrongValueException}
 * contract on {@code setResponsive} and {@code setResponsiveCol} (aligned
 * with ZK conventions used by {@code setMold} / {@code Radiogroup.setOrient} /
 * {@code Button.setType}).
 */
public class F104_ZK_5409_ValidationTest {

	// ── setResponsive: invalid values must throw ──

	@Test
	public void setResponsive_rejectsUpperCase() {
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		assertThrows(WrongValueException.class, () -> grid.setResponsive("STACKING"),
				"setResponsive(\"STACKING\") should reject — value matching is case-sensitive");
	}

	@Test
	public void setResponsive_rejectsTableLiteral() {
		// spec: there is intentionally no responsive="table" — table is the default
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		assertThrows(WrongValueException.class, () -> grid.setResponsive("table"));
	}

	@Test
	public void setResponsive_rejectsRandomString() {
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		assertThrows(WrongValueException.class, () -> grid.setResponsive("flex"));
	}

	// ── setResponsive: valid + normalization paths ──

	@Test
	public void setResponsive_acceptsStacking() {
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		grid.setResponsive("stacking");
		assertEquals("stacking", grid.getResponsive());
	}

	@Test
	public void setResponsive_acceptsNone() {
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		grid.setResponsive("none");
		assertEquals("none", grid.getResponsive());
	}

	@Test
	public void setResponsive_normalisesEmptyToNull() {
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		grid.setResponsive("stacking");
		grid.setResponsive("");
		assertNull(grid.getResponsive(),
				"Empty string should normalise to null (matches setSclass / setMold convention)");
	}

	@Test
	public void setResponsive_acceptsNullExplicitly() {
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		grid.setResponsive("stacking");
		grid.setResponsive(null);
		assertNull(grid.getResponsive());
	}

	// ── setResponsiveVisible: boolean contract ──

	@Test
	public void setResponsiveVisible_defaultsTrue() {
		org.zkoss.zul.Column col = new org.zkoss.zul.Column();
		assertTrue(col.isResponsiveVisible(),
				"Default visibility in stacking mode should be true (visible)");
	}

	@Test
	public void setResponsiveVisible_acceptsFalse() {
		org.zkoss.zul.Column col = new org.zkoss.zul.Column();
		col.setResponsiveVisible(false);
		assertFalse(col.isResponsiveVisible());
	}

	@Test
	public void setResponsiveVisible_acceptsTrueAfterFalse() {
		org.zkoss.zul.Column col = new org.zkoss.zul.Column();
		col.setResponsiveVisible(false);
		col.setResponsiveVisible(true);
		assertTrue(col.isResponsiveVisible());
	}

	// ── setResponsiveColumns: deliberately NOT validated (per-token client tolerance) ──

	@Test
	public void setResponsiveColumns_doesNotValidateTokens() {
		// Per design: server stores raw string; invalid tokens are silently
		// dropped by the client cascade resolver. Mirrors Column.setSort
		// which accepts arbitrary class-name strings.
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		grid.setResponsiveColumns("garbage-tokens-only");
		assertEquals("garbage-tokens-only", grid.getResponsiveColumns());
	}

	@Test
	public void setResponsiveColumns_normalisesEmptyToNull() {
		org.zkoss.zul.Grid grid = new org.zkoss.zul.Grid();
		grid.setResponsiveColumns("sm-1 md-none");
		grid.setResponsiveColumns("");
		assertNull(grid.getResponsiveColumns());
	}
}
