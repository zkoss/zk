/* MarbleBrand.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Sep 10, 2026 Created by hawkchen
}}IS_NOTE

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.theme;

import org.zkoss.zk.ui.util.Clients;

/**
 * Switches Marble's brand color between the built-in presets without the caller
 * having to know the underlying DOM detail.
 *
 * <p>Brand color in Marble is a declarative knob: a {@code data-brand} attribute
 * on the document root that overrides the primary <em>seed</em>
 * ({@code --zk-color-primary}). Because every container / on-container / filled
 * ({@code -fill}) partner, the state-layer overlays, the focus ring and
 * {@code --zk-color-inverse-primary} all derive from that seed via
 * {@code oklch(from …)}, flipping the one attribute re-derives a coherent palette.
 * See {@code tokens/_colors.css}.
 *
 * <p><b>Whole-app only.</b> Unlike {@link MarbleDensity} — whose size tokens are
 * re-pointed as literals so the attribute works at any scope — the brand presets
 * override only the seed and rely on the {@code oklch(from …)} derivations, which
 * are declared at {@code :root}. A {@code data-brand} placed on a descendant would
 * <em>not</em> re-derive the containers there (they resolved at {@code :root} and
 * inherit frozen), so this helper deliberately exposes a single whole-app method.
 * A brand is an app-wide identity, not a per-region concern.
 *
 * <p><b>When to use this vs. static CSS.</b> This API is for <em>runtime</em>
 * switching (a user picking a brand). For a customer's own fixed brand color,
 * prefer a static {@code :root { --zk-color-primary: #…; }} rule loaded after the
 * theme CSS (the {@code brand-override.md} recipe) — calling {@link #apply(Brand)}
 * on page load runs after the first paint and can cause a brief color flash (FOUC).
 *
 * @since 11.0.0
 */
public final class MarbleBrand {

	private MarbleBrand() {
	}

	/** A built-in brand preset. The token is what lands in the DOM as {@code data-brand}. */
	public enum Brand {
		/** Default Marble blue (#376fd0) — matches the theme's base :root seed. */
		DEFAULT("default"),
		/** Marine (#00729c). */
		MARINE("marine"),
		/** Slate (#506274). */
		SLATE("slate"),
		/** Copper (#b45309). */
		COPPER("copper"),
		/** Rose (#be185d). */
		ROSE("rose");

		private final String token;

		Brand(String token) {
			this.token = token;
		}

		/** The {@code data-brand} attribute value for this preset. */
		public String token() {
			return token;
		}
	}

	/**
	 * Applies the brand to the whole application by setting {@code data-brand} on
	 * the document root ({@code <html>}). Covers body-appended popups (menus, modal
	 * windows, notifications) too, since they inherit from the root.
	 *
	 * <p>{@link Brand#DEFAULT} removes the attribute so the base {@code :root} seed
	 * (Marble blue) takes over again. The root is not a ZK component, so this goes
	 * through {@link Clients#evalJavaScript(String)} and must run within an active
	 * ZK execution (e.g. inside an event listener or MVVM command).
	 *
	 * @param brand the brand to apply; never {@code null}.
	 */
	public static void apply(Brand brand) {
		String js = brand == Brand.DEFAULT
				? "document.documentElement.removeAttribute('data-brand')"
				: "document.documentElement.setAttribute('data-brand','" + brand.token() + "')";
		Clients.evalJavaScript(js);
	}
}
