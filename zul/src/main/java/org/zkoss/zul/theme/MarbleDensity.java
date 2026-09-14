/* MarbleDensity.java

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

/**
 * Switches Marble's data-dense (compact) mode without the caller having to know
 * the underlying DOM detail.
 *
 * <p>Density in Marble is a declarative knob: a {@code data-density} attribute
 * that overrides the semantic-layer size tokens (input height, row height, cell
 * padding, button/toolbar/tab heights, …). Because those are the tokens the
 * components actually read, the attribute takes effect at <em>any</em> scope —
 * the document root for the whole app, or a single component subtree for one
 * region.
 *
 * <p>This helper exposes that knob from Java:
 * <ul>
 *   <li>{@link #apply(Density)} — whole app. The attribute is set on the document
 *       root ({@code <html>}), which is not a ZK component, so this goes through
 *       {@link Clients#evalJavaScript(String)}.</li>
 *   <li>{@link #apply(Component, Density)} — one region. Uses ZK's native
 *       {@link Component#setClientDataAttribute(String, String)} (no JavaScript
 *       string), which re-renders that subtree when the value changes.</li>
 * </ul>
 *
 * <p><b>When to use this vs. a CSS preset.</b> This API is for <em>runtime</em>
 * switching (a user flipping a density preference). For a fixed whole-app
 * <em>default</em>, prefer the CSS preset ({@code html[data-density="compact"]},
 * shipped as {@code marble-compact.css}) or set the attribute server-side at
 * render time — calling {@link #apply(Density)} on page load runs after the first
 * paint and can cause a brief comfortable→compact flash (FOUC).
 *
 * @since 11.0.0
 */
public final class MarbleDensity {

	private MarbleDensity() {
	}

	/** The density a scope is rendered at. The token is what lands in the DOM as {@code data-density}. */
	public enum Density {
		/** Default comfortable density — matches the theme's :root token values. */
		COMFORTABLE("comfortable"),
		/** Data-dense mode — tighter control heights, row heights and cell padding. */
		COMPACT("compact");

		private final String token;

		Density(String token) {
			this.token = token;
		}

		/** The {@code data-density} attribute value for this density. */
		public String token() {
			return token;
		}
	}

	/**
	 * Applies the density to the whole application by setting {@code data-density}
	 * on the document root ({@code <html>}). Covers body-appended popups (menus,
	 * modal windows, notifications) too, since they inherit from the root.
	 *
	 * <p>Must run within an active ZK execution (e.g. inside an event listener or
	 * MVVM command) so the client update can be sent.
	 *
	 * @param density the density to apply; never {@code null}.
	 */
	public static void apply(Density density) {
		Clients.evalJavaScript(
				"document.documentElement.setAttribute('data-density','" + density.token() + "')");
	}

	/**
	 * Applies the density to a single component subtree by setting
	 * {@code data-density} on that component's DOM element. Use this to make one
	 * region (e.g. a data-dense grid panel) compact while the rest of the app stays
	 * comfortable; it nests and can be overridden by a closer descendant.
	 *
	 * @param scope   the component whose subtree should adopt the density.
	 * @param density the density to apply; never {@code null}.
	 */
	public static void apply(Component scope, Density density) {
		scope.setClientDataAttribute("density", density.token());
	}
}
