/* B110_ZK_6105Test.java

        Purpose:

        Description:

        History:
                Thu Aug 06 13:02:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6105: a CSS variable must name the role it fills.
 *
 * <p>Four kinds of decoupling are covered:
 * <ul>
 * <li>the mesh containers (listbox / grid / tree) take their background from a
 * mesh-family variable, not the unrelated {@code --zk-mask-background-color},
 * which has no fallback;</li>
 * <li>the checked-menuitem tick reads its own variable, not the surface colour
 * of an unchecked box;</li>
 * <li>the toolbar button, the slider knob and the mesh check mark read
 * component-scoped variables that default to the shared token they used to
 * borrow, so an existing override of the shared name still reaches them.</li>
 * <li>the confirmpopup surface and the avatargroup overlap are reachable from
 * the token file at all — one was a colour literal in the component LESS, the
 * other a default declared on the component's own class, which out-ranks a
 * {@code :root} override.</li>
 * </ul>
 *
 * <p>Every check runs through {@code assertAll} so that one unfixed decoupling
 * does not hide the state of the checks after it.
 */
public class B110_ZK_6105Test extends WebDriverTestCase {

	// #E0E1E3 — the container "extra space" fill introduced by ZK-5921.
	private static final String MESH_CONTAINER_BG = "rgb(224, 225, 227)";

	// the tick drawn on a filled checkbox: an on-accent foreground, not the
	// surface colour of an unchecked box.
	private static final String MENU_TICK = "rgb(255, 255, 255)";

	// --zk-button-border-width, the token the toolbar button used to borrow.
	private static final String TOOLBAR_BUTTON_BORDER_WIDTH = "2px";

	// --zk-color-primary, reached through --zk-button-background-color.
	private static final String SLIDER_BUTTON_BG = "rgb(0, 147, 249)";

	// --zk-checkbox-size, the token the mesh check mark used to borrow.
	private static final String MESH_CHECKABLE_SIZE = "20px";

	// --zk-input-font-size / --zk-input-border-radius, the tokens the buttons used to borrow.
	private static final String BUTTON_FONT_SIZE = "16px";
	private static final String BUTTON_BORDER_RADIUS = "4px";

	// --zk-carousel-control-hover-background-color, the arrow's hover fill.
	private static final String CAROUSEL_ARROW_HOVER_BG = "rgba(0, 0, 0, 0.6)";

	// --zk-avatargroup-overlap, the negative margin that stacks the avatars.
	private static final String AVATARGROUP_OVERLAP = "-8px";

	// --zk-popup-background-color, reached through --zk-confirmpopup-background-color.
	private static final String CONFIRMPOPUP_BG = "rgb(255, 255, 255)";

	@Test
	public void test() {
		connect();
		waitResponse();
		assertAll(
				() -> assertMeshContainerBackground("lb"),
				() -> assertMeshContainerBackground("gd"),
				() -> assertMeshContainerBackground("tr"),
				() -> assertAvatargroupOverlap(),
				// clicks to open, then closes again so the popup does not cover the
				// components checked after it.
				() -> assertConfirmpopupSurface(),
				() -> assertMenuCheckedTickColor(),
				() -> assertCheckboxTickColor(),
				() -> assertComponentScopedVariable("jq('$tbb')", "borderTopWidth",
						TOOLBAR_BUTTON_BORDER_WIDTH, "--zk-button-border-width",
						"--zk-toolbar-button-border-width", "7px"),
				() -> assertComponentScopedVariable("jq('$sl').find('.z-slider-button')",
						"backgroundColor", SLIDER_BUTTON_BG, "--zk-button-background-color",
						"--zk-slider-button-background-color", "rgb(1, 2, 3)"),
				() -> assertComponentScopedVariable("jq('$lic').find('.z-listitem-checkable')",
						"width", MESH_CHECKABLE_SIZE, "--zk-checkbox-size",
						"--zk-mesh-checkable-size", "7px"),
				() -> assertComponentScopedVariable("jq('$btn')", "fontSize",
						BUTTON_FONT_SIZE, "--zk-input-font-size",
						"--zk-button-font-size", "7px"),
				() -> assertComponentScopedVariable("jq('$btn')", "borderTopLeftRadius",
						BUTTON_BORDER_RADIUS, "--zk-input-border-radius",
						"--zk-button-border-radius", "7px"),
				() -> assertComponentScopedVariable("jq('$tbb')", "borderTopLeftRadius",
						BUTTON_BORDER_RADIUS, "--zk-input-border-radius",
						"--zk-toolbar-button-border-radius", "7px"),
				// last: hovering parks the pointer, which would perturb the checks above.
				() -> assertCarouselArrowHoverBackground());
	}

	/**
	 * Asserts a variable that a component gained instead of borrowing another
	 * component's, where the new one defaults to the token it replaced. Unlike
	 * the mesh background below, the old name must keep reaching the component.
	 */
	private void assertComponentScopedVariable(String selector, String cssProp,
			String expected, String sharedVar, String scopedVar, String probe) {
		assertAll(
				// 1) regression guard: the rendered value stays the same after the refactor.
				() -> assertEquals(expected, computedStyle(selector, cssProp),
						scopedVar + ": rendered value changed"),
				// 2) the fallback is intact: overriding the shared token at :root must
				// still reach the component, so existing customizations keep working.
				() -> {
					setRootVar(sharedVar, probe);
					try {
						assertEquals(probe, computedStyle(selector, cssProp),
								scopedVar + " does not default to " + sharedVar);
					} finally {
						removeRootVar(sharedVar);
					}
				},
				// 3) the new override point works: the component-scoped name drives it.
				() -> {
					setRootVar(scopedVar, probe);
					try {
						assertEquals(probe, computedStyle(selector, cssProp),
								selector + " does not read " + scopedVar);
					} finally {
						removeRootVar(scopedVar);
					}
				});
	}

	/**
	 * The arrow's hover fill was the LESS file's only colour literal, so no theme
	 * could reach it at all. It now has its own token — note that a theme must set
	 * it explicitly; it does not derive from the resting-state token.
	 */
	private void assertCarouselArrowHoverBackground() {
		String arrow = "jq('$cr').find('.z-carousel-arrow-prev')";
		mouseOver(jq("$cr").find(".z-carousel-arrow-prev"));

		assertAll(
				// 1) regression guard: the hover fill is unchanged by the refactor.
				() -> assertEquals(CAROUSEL_ARROW_HOVER_BG, computedStyle(arrow, "backgroundColor"),
						"carousel arrow hover background changed"),
				// 2) the override point works: the variable must drive the hover fill.
				() -> {
					setRootVar("--zk-carousel-control-hover-background-color", "rgb(1, 2, 3)");
					try {
						assertEquals("rgb(1, 2, 3)", computedStyle(arrow, "backgroundColor"),
								"carousel arrow hover does not read "
										+ "--zk-carousel-control-hover-background-color");
					} finally {
						removeRootVar("--zk-carousel-control-hover-background-color");
					}
				});
	}

	/**
	 * The overlap had its base default on {@code .z-avatargroup} itself, which
	 * out-ranks a {@code :root} override, so no theme could retune the stack.
	 * The per-size cascade keeps its own value and is out of scope here.
	 */
	private void assertAvatargroupOverlap() {
		String second = "jq('$ag').find('.z-avatar:last')";

		assertAll(
				// 1) regression guard: the stack keeps its spacing after the move.
				() -> assertEquals(AVATARGROUP_OVERLAP, computedStyle(second, "marginLeft"),
						"avatargroup overlap changed"),
				// 2) a guard, not the driver: the first avatar stays flush either way.
				() -> assertEquals("0px",
						computedStyle("jq('$ag').find('.z-avatar:first')", "marginLeft"),
						"the first avatar must start flush"),
				// 3) the override point works: :root must reach the overlap.
				() -> {
					setRootVar("--zk-avatargroup-overlap", "-20px");
					try {
						assertEquals("-20px", computedStyle(second, "marginLeft"),
								"--zk-avatargroup-overlap is not reachable from :root");
					} finally {
						removeRootVar("--zk-avatargroup-overlap");
					}
				});
	}

	/**
	 * The popup surface was a colour literal, so no palette could repaint it.
	 * Nothing regresses today — the themepack template ships no confirmpopup —
	 * but a dark palette would paint white-on-white once it does.
	 */
	private void assertConfirmpopupSurface() {
		click(jq("$cpb"));
		waitResponse();
		try {
			assertAll(
					() -> assertComponentScopedVariable("jq('$cp')", "backgroundColor",
							CONFIRMPOPUP_BG, "--zk-popup-background-color",
							"--zk-confirmpopup-background-color", "rgb(1, 2, 3)"),
					() -> assertConfirmpopupArrowFill());
		} finally {
			click(jq("$cp").find(".z-confirmpopup-cancel"));
			waitResponse();
		}
	}

	/**
	 * The arrow is an empty span whose fill lives only on {@code ::after}, and it
	 * is a separate declaration from the surface — tokenising the body alone
	 * would leave four white triangles behind.
	 */
	private void assertConfirmpopupArrowFill() {
		String arrow = "jq('$cp').find('.z-confirmpopup-arrow')";

		assertAll(
				// 1) regression guard: the arrow keeps the surface colour.
				() -> assertEquals(CONFIRMPOPUP_BG,
						computedStyle(arrow, "::after", "borderTopColor"),
						"confirmpopup arrow fill changed"),
				// 2) the override point works: the arrow follows the surface token.
				() -> {
					setRootVar("--zk-confirmpopup-background-color", "rgb(1, 2, 3)");
					try {
						assertEquals("rgb(1, 2, 3)",
								computedStyle(arrow, "::after", "borderTopColor"),
								"confirmpopup arrow does not read "
										+ "--zk-confirmpopup-background-color");
					} finally {
						removeRootVar("--zk-confirmpopup-background-color");
					}
				});
	}

	private String computedStyle(String selector, String cssProp) {
		return getEval("getComputedStyle(" + selector + "[0])." + cssProp);
	}

	private String computedStyle(String selector, String pseudo, String cssProp) {
		return getEval("getComputedStyle(" + selector + "[0],'" + pseudo
				+ "')." + cssProp);
	}

	/**
	 * The checked-menuitem tick used to read {@code --zk-checked-background-color}
	 * (the unchecked box surface) purely because both happened to be white.
	 */
	private void assertMenuCheckedTickColor() {
		click(jq("$mn"));
		waitResponse();

		assertAll(
				// 1) regression guard: the tick keeps its colour after the refactor.
				() -> assertEquals(MENU_TICK, tickColor(), "menu checked tick colour changed"),
				// 2) semantic decoupling (the ZK-6105 fix): the tick must no longer
				// follow the checkbox surface variable.
				() -> {
					setRootVar("--zk-checked-background-color", "rgb(255, 0, 0)");
					try {
						assertEquals(MENU_TICK, tickColor(),
								"menu tick still reads --zk-checked-background-color");
					} finally {
						removeRootVar("--zk-checked-background-color");
					}
				},
				// 3) positive coupling: its own variable must drive it.
				() -> {
					setRootVar("--zk-menu-checked-color", "rgb(1, 2, 3)");
					try {
						assertEquals("rgb(1, 2, 3)", tickColor(),
								"menu tick does not read --zk-menu-checked-color");
					} finally {
						removeRootVar("--zk-menu-checked-color");
					}
				});
	}

	/**
	 * The tick on a filled checkbox reads its own foreground token, not the surface
	 * colour of an unchecked box.
	 */
	private void assertCheckboxTickColor() {
		assertAll(
				// 1) regression guard: the tick keeps its colour after the refactor.
				() -> assertEquals(MENU_TICK, checkboxTickColor(), "checkbox tick colour changed"),
				// 2) semantic decoupling (the ZK-6105 fix): the tick must no longer
				// follow the unchecked box surface variable.
				() -> {
					setRootVar("--zk-checked-background-color", "rgb(255, 0, 0)");
					try {
						assertEquals(MENU_TICK, checkboxTickColor(),
								"checkbox tick still reads --zk-checked-background-color");
					} finally {
						removeRootVar("--zk-checked-background-color");
					}
				},
				// 3) positive coupling: its own variable must drive it.
				() -> {
					setRootVar("--zk-checked-icon-color", "rgb(1, 2, 3)");
					try {
						assertEquals("rgb(1, 2, 3)", checkboxTickColor(),
								"checkbox tick does not read --zk-checked-icon-color");
					} finally {
						removeRootVar("--zk-checked-icon-color");
					}
				});
	}

	private String checkboxTickColor() {
		return getEval("getComputedStyle(jq('$cb').find('input')[0]).color");
	}

	private String tickColor() {
		return getEval("getComputedStyle(jq('$mi').find('.z-menuitem-icon')[0]).color");
	}

	private void setRootVar(String name, String value) {
		getEval("(function(){document.documentElement.style.setProperty('" + name
				+ "','" + value + "');return '';})()");
	}

	private void removeRootVar(String name) {
		getEval("(function(){document.documentElement.style.removeProperty('" + name
				+ "');return '';})()");
	}

	private void assertMeshContainerBackground(String id) {
		assertAll(
				// 1) regression guard: the container fill stays the same after the refactor.
				() -> assertEquals(MESH_CONTAINER_BG, computedBg(id),
						id + " container background changed"),
				// 2) semantic decoupling (the actual ZK-6105 fix): overriding the mask
				// variable must NOT repaint the mesh container.
				() -> {
					try {
						assertEquals(MESH_CONTAINER_BG,
								bgAfterElementOverride(id, "--zk-mask-background-color",
										"rgb(255, 0, 0)"),
								id + " still reads --zk-mask-background-color (not decoupled from mask)");
					} finally {
						removeElementVar(id, "--zk-mask-background-color");
					}
				},
				// 3) positive coupling: overriding the mesh-family variable MUST repaint
				// the container, proving the background is driven by that variable.
				() -> {
					try {
						assertEquals("rgb(1, 2, 3)",
								bgAfterElementOverride(id, "--zk-mesh-outer-background-color",
										"rgb(1, 2, 3)"),
								id + " does not read --zk-mesh-outer-background-color");
					} finally {
						removeElementVar(id, "--zk-mesh-outer-background-color");
					}
				});
	}

	private String bgAfterElementOverride(String id, String name, String value) {
		return getEval("(function(){var e=jq('$" + id + "')[0];"
				+ "e.style.setProperty('" + name + "','" + value + "');"
				+ "return getComputedStyle(e).backgroundColor;})()");
	}

	private void removeElementVar(String id, String name) {
		getEval("(function(){jq('$" + id + "')[0].style.removeProperty('" + name
				+ "');return '';})()");
	}

	private String computedBg(String id) {
		return getEval("getComputedStyle(jq('$" + id + "')[0]).backgroundColor");
	}
}
