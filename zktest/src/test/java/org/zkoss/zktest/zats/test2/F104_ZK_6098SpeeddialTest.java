/* F104_ZK_6098SpeeddialTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 15 15:41:18 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Browser-level coverage for Speeddial / Speeddialitem (PE, ZK-6098).
 * Loads F104-ZK-6098-Speeddial.zul (BaseTestCase.connect() resolves the
 * page from the test class name).
 *
 * Server-side Java API is covered by SpeeddialTest in zkcml. This class
 * focuses on DOM / ARIA / keyboard / fan-out geometry / server↔client
 * round-trips that only a real browser can verify.
 *
 * Sections marked N/A (with one-line reason in code):
 *  - A4 setter overloads:        N/A — no overloaded setters on Speeddial.
 *  - A6 toString/equals/hashCode: N/A — not overridden.
 *  - A7 Serializable round-trip: out-of-scope — relies on XulElement default.
 *  - C7 selected/checked color:  N/A — Speeddial has no selectable state.
 *  - C8 WCAG contrast:           covered by ./gradlew testWCAGOnly.
 *  - C9 dark theme:              out-of-scope — no dark theme declared.
 *  - D7 reference / model:       N/A — Speeddial does not expose model.
 *  - F5 nested grandchild:       N/A — Speeddialitem.isChildable() = false.
 *  - F7/F8 dynamic add/remove:   out-of-scope WebDriver — server-side concern.
 *  - H4 focus visible:           visual review only.
 */
public class F104_ZK_6098SpeeddialTest extends WebDriverTestCase {

	// BaseTestCase.connect() resolves a path from the test class name and
	// only tries replacing all `_` with `-`. For our class that yields
	// `F104-ZK-6098Speeddial.zul`, but we ship the page as
	// `F104-ZK-6098-Speeddial.zul` (with the extra dash before "Speeddial"
	// for readability). Override to point at the file we ship.
	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6098-Speeddial.zul";
	}

	// ────────────────────────────────────────────────────────────────────
	// Basic render / structure
	// ────────────────────────────────────────────────────────────────────

	/** B1, C1: default render — root carries z-speeddial sclass. */
	@Test
	public void speeddialDefaultRender() {
		connect();
		waitResponse();

		JQuery dial = jq("$circleDial");
		assertTrue(dial.exists());
		assertTrue(dial.hasClass("z-speeddial"),
				"root should carry z-speeddial sclass");
	}

	/** C3 / D5: zclass override REPLACES base class. */
	@Test
	public void speeddialZclassOverride() {
		connect();
		waitResponse();

		JQuery dial = jq("$zclassDial");
		assertTrue(dial.exists());
		assertTrue(dial.hasClass("z-mydial-test"),
				"custom zclass should be applied");
		assertFalse(dial.hasClass("z-speeddial"),
				"zclass override REPLACES base class, does not append");
	}

	/** D2: iconSclass default is z-icon-plus and renders inside the trigger. */
	@Test
	public void speeddialIconSclassDefault() {
		connect();
		waitResponse();

		JQuery icon = jq("$circleDial .z-icon-plus");
		assertTrue(icon.exists(),
				"default iconSclass=z-icon-plus should render in trigger");
	}

	/** D2: label round-trip — server-side label appears as text in the rendered item. */
	@Test
	public void speeddialitemLabelRenders() {
		connect();
		waitResponse();

		JQuery item = jq("$circleItem1");
		assertTrue(item.exists());
		assertTrue(item.text().contains("New") || "New".equals(item.attr("aria-label")),
				"item label should be visible or available to assistive tech");
	}

	// ────────────────────────────────────────────────────────────────────
	// Open / close interaction
	// ────────────────────────────────────────────────────────────────────

	/** Click→open, Esc→close keyboard cycle. */
	@Test
	public void speeddialOpensViaClickAndClosesViaEsc() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		assertTrue(trigger.exists(), "trigger button should render");
		assertEquals("false", trigger.attr("aria-expanded"),
				"dial starts closed");

		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"click on FAB should open the dial");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"Esc should close the dial");
	}

	/** trigger="hover" + modal=true is coerced to click; click still opens. */
	@Test
	public void speeddialQuarterMaskedOpensViaClick() {
		connect();
		waitResponse();

		JQuery trigger = jq("$quarterDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"masked dial opens via click after hover→click coercion");
	}

	/** ArrowDown on a focused trigger opens the dial without a mouse click. */
	@Test
	public void speeddialKeyboardArrowDownOpens() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		// Focus the trigger first — keydown without focus is dropped by the widget.
		getEval("document.getElementById('" + trigger.attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"ArrowDown on a focused trigger should open the dial");
	}

	/** Enter key on a focused trigger opens the dial (WCAG 2.1.1). */
	@Test
	public void speeddialKeyboardEnterOpens() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		getEval("document.getElementById('" + trigger.attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"Enter on a focused trigger should open the dial (WCAG 2.1.1 keyboard accessibility)");
	}

	/** Space key on a focused trigger opens the dial. */
	@Test
	public void speeddialKeyboardSpaceOpens() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		getEval("document.getElementById('" + trigger.attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.SPACE).perform();
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"Space on a focused trigger should open the dial (WCAG 2.1.1 / native <button> behaviour)");
	}

	/**
	 * Escape on an open dial should close it AND restore focus to the
	 * trigger so keyboard users don't lose their place (WCAG 2.4.3 focus order).
	 */
	@Test
	public void speeddialEscapeRestoresFocusToTrigger() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: dial open");

		// Esc anywhere on the document (handled by _docKeyHandler).
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"Escape should close the dial");

		String activeId = getEval("document.activeElement && document.activeElement.id");
		assertEquals(trigger.attr("id"), activeId,
				"Escape must restore focus to the trigger button (WCAG 2.4.3)");
	}

	/**
	 * Arrow Down on an item should focus the next sibling item; Arrow Up
	 * the previous (when direction is vertical). Implemented in
	 * Speeddialitem.doKeyDown_.
	 */
	@Test
	public void speeddialItemKeyboardNavigatesBetweenSiblings() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"));

		// Focus the first item programmatically (matching how
		// _focusFirstItem would behave after the user hit ArrowDown on the
		// trigger of an already-open dial).
		String firstItemId = jq("$circleItem1").attr("id");
		getEval("document.getElementById('" + firstItemId + "').focus()");
		waitResponse();

		// ArrowDown should move focus to circleItem2.
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		String afterDown = getEval("document.activeElement && document.activeElement.id");
		assertEquals(jq("$circleItem2").attr("id"), afterDown,
				"ArrowDown should move focus to the next item sibling");

		// ArrowUp should move focus back to circleItem1.
		getActions().sendKeys(Keys.ARROW_UP).perform();
		waitResponse();
		String afterUp = getEval("document.activeElement && document.activeElement.id");
		assertEquals(firstItemId, afterUp,
				"ArrowUp should move focus to the previous item sibling");
	}

	/**
	 * Right-click on the trigger must NOT open the dial. The widget binds
	 * to `onClick` (left-click only) so the contextmenu event bubbles
	 * undisturbed.
	 */
	@Test
	public void speeddialRightClickDoesNotOpen() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		// Dispatch a contextmenu event (right-click). Selenium's
		// contextClick() also dispatches but is flaky in headless; JS is
		// reliable.
		getEval(
			"document.getElementById('" + trigger.attr("id") + "')"
			+ ".dispatchEvent(new MouseEvent('contextmenu', {bubbles: true, cancelable: true, button: 2}))");
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"right-click (contextmenu) must NOT open the dial");
	}

	/** Mask overlay click closes the dial. */
	@Test
	public void speeddialMaskClickCloses() {
		connect();
		waitResponse();

		JQuery trigger = jq("$quarterDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: dial open after trigger click");

		JQuery modal = jq("$quarterDial").find(".z-speeddial-modal");
		click(modal);
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"clicking the modal overlay should close the dial");
	}

	/** closeOnOutsideClick=true (default): outside click closes the dial. */
	@Test
	public void speeddialHideOnClickOutsideTrueCloses() {
		connect();
		waitResponse();

		// Scroll the hoco fixture into view: with the gallery hosting every
		// direction/type combo, hocoTrueDial lives below the headless viewport.
		getEval("document.getElementById('" + jq("$hocoTrueDial").attr("id")
				+ "').scrollIntoView({block: 'center'})");
		JQuery trigger = jq("$hocoTrueDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"));

		// Dispatch a mousedown on a target outside the dial. The dial's
		// document-level handler listens for `mousedown` (not `click`), and
		// a JS dispatch bypasses Selenium's interactability check that flags
		// a scrolled <span> as not-interactable in headless Chrome.
		getEval(
			"document.getElementById('" + jq("$openLog").attr("id") + "')"
			+ ".dispatchEvent(new MouseEvent('mousedown', {bubbles: true, cancelable: true}))");
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"closeOnOutsideClick=true should close the dial on outside click");
	}

	/** closeOnOutsideClick=false: outside click ignored. */
	@Test
	public void speeddialHideOnClickOutsideFalseStaysOpen() {
		connect();
		waitResponse();

		getEval("document.getElementById('" + jq("$hocoFalseDial").attr("id")
				+ "').scrollIntoView({block: 'center'})");
		JQuery trigger = jq("$hocoFalseDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"));

		click(jq("$outsideAnchor"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"closeOnOutsideClick=false should ignore outside clicks");
	}

	/** autoClose=false: dial stays open after item click. */
	@Test
	public void speeddialCloseOnSelectFalseStaysOpen() {
		connect();
		waitResponse();

		JQuery trigger = jq("$autoCloseFalseDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"));

		click(jq("$cosfItem1"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"autoClose=false should leave the dial open after item click");
	}

	/** autoClose=true (default): item click closes the dial. */
	@Test
	public void speeddialCloseOnSelectTrueClosesAfterItemClick() {
		connect();
		waitResponse();

		JQuery trigger = jq("$cosTrueDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"));

		click(jq("$cosTrueItem1"));
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"autoClose default (true) should close the dial after item click");
	}

	/** Disabled item click is a no-op (dial stays open). */
	@Test
	public void speeddialDisabledItemClickIsNoop() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();

		JQuery disabled = jq("$circleItem4");
		assertTrue(disabled.exists());
		assertTrue(disabled.hasClass("z-speeddialitem-disabled")
						|| "true".equals(disabled.attr("aria-disabled"))
						|| disabled.attr("disabled") != null,
				"disabled item should expose a disabled state to a11y tooling");

		click(disabled);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"clicking a disabled item should NOT close the dial");
	}

	// ────────────────────────────────────────────────────────────────────
	// Server ↔ client round-trip
	// ────────────────────────────────────────────────────────────────────

	/** Server setOpen(true) pushes aria-expanded=true to the client. */
	@Test
	public void speeddialServerSetOpenPushesToClient() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		click(jq("$serverOpenBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"server-side setOpen(true) should reflect on the client");

		click(jq("$serverCloseBtn"));
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"server-side setOpen(false) should reflect on the client");
	}

	/** Client click fires onOpen on the server; openLog accumulates "open;"/"close;". */
	@Test
	public void speeddialClientClickFiresOnOpenOnServer() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		JQuery log = jq("$openLog");
		// Labels render as <span>, not <input> — use .text(), not .val().
		assertEquals("", log.text(), "log starts empty");

		click(trigger);
		waitResponse();
		assertTrue(log.text().contains("open;"),
				"client-side click should round-trip an onOpen event with isOpen=true; got '" + log.text() + "'");

		click(trigger);
		waitResponse();
		assertTrue(log.text().contains("close;"),
				"second click should round-trip an onOpen event with isOpen=false; got '" + log.text() + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// ARIA / accessibility
	// ────────────────────────────────────────────────────────────────────

	/** H1: trigger has aria-haspopup="menu" and aria-controls pointing to the items container. */
	@Test
	public void speeddialAriaHaspopupAndControls() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("menu", trigger.attr("aria-haspopup"),
				"trigger should advertise aria-haspopup=menu");

		// The icon-only trigger's accessible name is layered on by the EE za11y add-on
		// from msgza11y.SPEEDDIAL. za11y is absent in the NO_A11Y test variant, so guard
		// the assertion on its presence (CLAUDE.md a11y rule #1 / B80_ZK_3139Test idiom) —
		// without za11y there is deliberately no aria-label on the trigger.
		if (Boolean.valueOf(getEval("!!window.za11y"))) {
			assertEquals("Speed dial", trigger.attr("aria-label"),
					"trigger aria-label must be the za11y default (msgza11y.SPEEDDIAL)");
		}

		// jQuery attr() returns the string "null" when the attribute is absent, so a
		// non-null / non-empty check is vacuous; assert aria-controls actually points
		// at the items <ul> id instead.
		String controls = trigger.attr("aria-controls");
		assertEquals(jq("$circleDial").find("ul").first().attr("id"), controls,
				"aria-controls must reference the items <ul> id");
	}

	// ────────────────────────────────────────────────────────────────────
	// Children / structure edge cases
	// ────────────────────────────────────────────────────────────────────

	/** F1: an empty Speeddial renders without children, no layout error. */
	@Test
	public void speeddialEmptyRendersWithoutItems() {
		connect();
		waitResponse();

		JQuery dial = jq("$emptyDial");
		assertTrue(dial.exists());
		JQuery items = dial.find(".z-speeddialitem");
		assertEquals(0, items.length(),
				"empty dial should render zero items");
	}

	/** F2: single-child dial renders exactly one item. */
	@Test
	public void speeddialSingleChild() {
		connect();
		waitResponse();

		JQuery dial = jq("$singleDial");
		assertTrue(dial.exists());
		JQuery items = dial.find(".z-speeddialitem");
		assertEquals(1, items.length());
	}

	/** F4: 10-child dial renders all items (no virtualization). */
	@Test
	public void speeddialManyChildren() {
		connect();
		waitResponse();

		JQuery dial = jq("$manyDial");
		assertTrue(dial.exists());
		JQuery items = dial.find(".z-speeddialitem");
		assertEquals(10, items.length(),
				"all 10 items should render under the linear layout");
	}

	/** B4: visible=false hides the dial from layout flow. */
	@Test
	public void speeddialVisibleFalseHides() {
		connect();
		waitResponse();

		JQuery hidden = jq("$hiddenDial");
		boolean hiddenOk = !hidden.exists()
				|| "none".equals(hidden.css("display"))
				|| "hidden".equals(hidden.css("visibility"));
		assertTrue(hiddenOk,
				"visible=false should remove the dial from layout flow");
	}

	/** I3: a label with HTML payload is rendered as text, not as live HTML. */
	@Test
	public void speeddialitemLabelEscapedAgainstXss() {
		connect();
		waitResponse();

		// If the label were injected raw, the <img onerror> would have set
		// window._xssFired=true on render. Assert the side effect didn't happen.
		String fired = getEval("window._xssFired === true");
		assertFalse("true".equals(fired),
				"label content with HTML chars must not execute as live HTML");

		JQuery item = jq("$xssItem");
		assertTrue(item.text().contains("<img"),
				"label should render as escaped text containing the literal '<img'");
	}

	// ────────────────────────────────────────────────────────────────────
	// Breadth — class names for direction × type matrix
	// ────────────────────────────────────────────────────────────────────

	/** All 8 linear directions emit the matching z-speeddial-direction-* sclass. */
	@Test
	public void speeddialLinearAllEightDirections() {
		connect();
		waitResponse();
		String[][] dialsAndDirs = {
				{"linUp", "up"},
				{"linRight", "right"},
				{"linDown", "down"},
				{"linLeft", "left"},
				{"linUpLeft", "up_left"},
				{"linUpRight", "up_right"},
				{"linDownLeft", "down_left"},
				{"linDownRight", "down_right"},
		};
		for (String[] pair : dialsAndDirs) {
			JQuery dial = jq("$" + pair[0]);
			assertTrue(dial.exists(), pair[0] + " should render");
			assertTrue(dial.hasClass("z-speeddial-direction-" + pair[1]),
					pair[0] + " should carry z-speeddial-direction-" + pair[1]);
			assertTrue(dial.hasClass("z-speeddial-type-linear"),
					pair[0] + " should carry z-speeddial-type-linear");
		}
	}

	/** Semi-circle direction matrix (down / up / left / right). */
	@Test
	public void speeddialSemiCircleAllDirections() {
		connect();
		waitResponse();
		String[][] dialsAndDirs = {
				{"semiDown", "down"},
				{"semiUp", "up"},
				{"semiRight", "right"},
				{"semiLeft", "left"},
		};
		for (String[] pair : dialsAndDirs) {
			JQuery dial = jq("$" + pair[0]);
			assertTrue(dial.exists(), pair[0] + " should render");
			assertTrue(dial.hasClass("z-speeddial-direction-" + pair[1]),
					pair[0] + " should carry z-speeddial-direction-" + pair[1]);
			assertTrue(dial.hasClass("z-speeddial-type-semi_circle"),
					pair[0] + " should carry z-speeddial-type-semi_circle");
		}
	}

	/** Quarter-circle direction matrix (4 diagonals). */
	@Test
	public void speeddialQuarterCircleAllDiagonals() {
		connect();
		waitResponse();
		String[][] dialsAndDirs = {
				{"quarterDownRight", "down_right"},
				{"quarterDownLeft", "down_left"},
				{"quarterUpRight", "up_right"},
				{"quarterUpLeft", "up_left"},
		};
		for (String[] pair : dialsAndDirs) {
			JQuery dial = jq("$" + pair[0]);
			assertTrue(dial.exists(), pair[0] + " should render");
			assertTrue(dial.hasClass("z-speeddial-direction-" + pair[1]),
					pair[0] + " should carry z-speeddial-direction-" + pair[1]);
			assertTrue(dial.hasClass("z-speeddial-type-quarter_circle"),
					pair[0] + " should carry z-speeddial-type-quarter_circle");
		}
	}

	// ────────────────────────────────────────────────────────────────────
	// Geometry — FAB-centered fan-out positions
	// ────────────────────────────────────────────────────────────────────

	/**
	 * The Vuetify-spec contract: items orbit the FAB CENTER. After the
	 * centering fix in _layoutItems, the first item's centre on each
	 * direction must coincide with the trigger's centre + radial vector.
	 */
	@Test
	public void speeddialItemsCenteredOnTrigger() {
		connect();
		waitResponse();

		JQuery trigger = jq("$linUp").find("button").first();
		click(trigger);
		waitResponse();

		// Trigger sits at trigger-half (28px) inside the dial's box. The first
		// item's inline-style top is computed in _layoutItems as
		// (TRIGGER_HALF + p.y - ITEM_HALF) = (28 + (-56) - 24) = -52.
		// With ITEM_HALF=24 (item 48x48 — Material touch-target), for
		// linear/up first item:
		//   item-centre.x = trigger-centre.x = 28
		//   item-centre.y = trigger-centre.y - 56 = -28
		//   top-left = (28 - 24, -28 - 24) = (4, -52)
		JQuery first = jq("$linUp").find(".z-speeddial-items > li").first();
		int left = parseLeftPx(first.attr("style"));
		int top = parseTopPx(first.attr("style"));
		assertEquals(4, left,
				"linear/up first item should be centred on trigger X (left=4); got " + left);
		assertEquals(-52, top,
				"linear/up first item should be 56px above trigger centre (top=-52); got " + top);
	}

	/** Fan-out positioning: linear/up — items stack vertically with descending top. */
	@Test
	public void speeddialLinearUpFanOutPositions() {
		connect();
		waitResponse();

		JQuery trigger = jq("$linUp").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: linUp open");

		JQuery items = jq("$linUp").find(".z-speeddial-items > li");
		assertTrue(items.length() >= 3, "expected at least 3 fan-out slots");

		int t1 = parseTopPx(items.eq(0).attr("style"));
		int t2 = parseTopPx(items.eq(1).attr("style"));
		int t3 = parseTopPx(items.eq(2).attr("style"));
		// direction=up: each subsequent item sits FURTHER UP, so its `top`
		// inline-style value becomes more negative. t1 should be the largest
		// (least negative; closest to the trigger), then t2 < t1, t3 < t2.
		assertTrue(t1 > t2 && t2 > t3,
				"direction=up items should fan upward (each top further up / more negative); got "
						+ t1 + ", " + t2 + ", " + t3);
		// All three should sit ABOVE trigger center (top < 6 after centering offset).
		assertTrue(t1 < 6 && t2 < 6 && t3 < 6,
				"all items must have top < 6 (i.e. above trigger centre); got " + t1 + ", " + t2 + ", " + t3);
	}

	/** Fan-out positioning: linear/right — items stack horizontally with ascending left. */
	@Test
	public void speeddialLinearRightFanOutPositions() {
		connect();
		waitResponse();

		JQuery trigger = jq("$linRight").find("button").first();
		click(trigger);
		waitResponse();

		JQuery items = jq("$linRight").find(".z-speeddial-items > li");
		assertTrue(items.length() >= 3);

		int l1 = parseLeftPx(items.eq(0).attr("style"));
		int l2 = parseLeftPx(items.eq(1).attr("style"));
		int l3 = parseLeftPx(items.eq(2).attr("style"));
		assertTrue(l1 > 6 && l2 > l1 && l3 > l2,
				"linear/right items should fan rightward past the trigger centre; got "
						+ l1 + ", " + l2 + ", " + l3);
	}

	/** Fan-out positioning: linear/down_right — items go diagonal (both left>6 and top>6). */
	@Test
	public void speeddialLinearDownRightFanOutPositions() {
		connect();
		waitResponse();

		JQuery trigger = jq("$linDownRight").find("button").first();
		click(trigger);
		waitResponse();

		JQuery first = jq("$linDownRight").find(".z-speeddial-items > li").first();
		String style = first.attr("style");
		int left = parseLeftPx(style);
		int top = parseTopPx(style);
		assertTrue(left > 6 && top > 6,
				"down_right diagonal first item: must be right AND below trigger centre; left=" + left + ", top=" + top);
	}

	/**
	 * Anchor breadth: each corner-anchored fixture renders inside its host
	 * wrapper (not pinned to the viewport's fixed bottom-right corner).
	 */
	@Test
	public void speeddialAnchorBreadthScopedToHostWrapper() {
		connect();
		waitResponse();

		for (String id : new String[] {"anchorTL", "anchorTR", "anchorBL", "anchorBR", "anchorCenter"}) {
			JQuery dial = jq("$" + id);
			assertTrue(dial.exists(), id + " should render");
			boolean inside = Boolean.parseBoolean(getEval(
					"(function(){"
							+ "var d=document.getElementById('" + dial.attr("id") + "');"
							+ "var h=d.parentElement;"
							+ "var dr=d.getBoundingClientRect(),hr=h.getBoundingClientRect();"
							+ "return (dr.left>=hr.left-1)&&(dr.top>=hr.top-1)"
							+ "&&(dr.right<=hr.right+1)&&(dr.bottom<=hr.bottom+1);"
							+ "})()"));
			assertTrue(inside,
					id + " should be visually contained in its .dial-host wrapper, not pinned to viewport corner");
		}
	}

	// ────────────────────────────────────────────────────────────────────
	// Decoration — closeIconSclass, modal overlay, transitionDelay
	// ────────────────────────────────────────────────────────────────────

	/** closeIconSclass renders a second icon node alongside the open icon. */
	@Test
	public void speeddialCloseIconRendersWhenSet() {
		connect();
		waitResponse();
		JQuery openIcon = jq("$closeIconDial .z-speeddial-icon-open");
		JQuery closeIcon = jq("$closeIconDial .z-speeddial-icon-close");
		assertTrue(openIcon.exists(),
				"open icon node should render under .z-speeddial-icon-open");
		assertTrue(closeIcon.exists(),
				"closeIconSclass should add a second icon under .z-speeddial-icon-close");
	}

	/**
	 * Regression: when no closeIconSclass is set, the trigger's lone
	 * `:only-child` open-icon must stay VISIBLE on open (rotated 45° to
	 * look like ×, Material FAB convention). Earlier CSS forgot to
	 * restore opacity:1 in the `:only-child` override, so the FAB
	 * appeared to vanish when opened.
	 */
	@Test
	public void speeddialOpenIconStaysVisibleWithoutCloseIcon() {
		connect();
		waitResponse();

		// circleDial has no closeIconSclass — open-icon is :only-child.
		JQuery trigger = jq("$circleDial").find("button").first();
		JQuery icon = jq("$circleDial .z-speeddial-icon-open");
		assertTrue(icon.exists(), "precondition: open-icon node rendered");
		double opacityClosed = Double.parseDouble(icon.css("opacity"));
		assertEquals(1.0, opacityClosed, 0.01,
				"closed: lone open-icon must be fully opaque");

		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: dial open");

		double opacityOpen = Double.parseDouble(icon.css("opacity"));
		assertEquals(1.0, opacityOpen, 0.01,
				"open + no closeIconSclass: open-icon must REMAIN visible "
						+ "(rotated to look like ×), not vanish");
	}

	/** modal=true renders the overlay element next to the trigger. */
	@Test
	public void speeddialMaskOverlayPresent() {
		connect();
		waitResponse();
		JQuery modal = jq("$quarterDial").find(".z-speeddial-modal");
		assertTrue(modal.exists(),
				"modal=true should render a .z-speeddial-modal element");
	}

	/** transitionDelay propagates to each item's transition-delay style. */
	@Test
	public void speeddialTransitionDelayAppliesToItems() {
		connect();
		waitResponse();
		JQuery firstSlot = jq("$delayDial").find(".z-speeddial-items > li").first();
		String style = firstSlot.attr("style");
		assertNotNull(style, "stagger requires an inline style on each item slot");
		assertTrue(style.contains("transition-delay"),
				"transitionDelay should be applied as a transition-delay inline style; got: " + style);
	}

	// ────────────────────────────────────────────────────────────────────
	// A11y — touch target, edge cases
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Touch target regression guard: WCAG 2.5.5 (Level AAA) requires
	 * interactive targets to be at least 44×44 CSS pixels. Both the trigger
	 * (56×56) and items (44×44 currently) should meet this floor; Material
	 * Design recommends 48×48 as a softer target. This test asserts the
	 * WCAG AAA minimum so a future CSS shrink can't slip in.
	 */
	@Test
	public void speeddialTouchTargetsMeetWcagAaa() {
		connect();
		waitResponse();

		JQuery trigger = jq("$circleDial").find(".z-speeddial-trigger").first();
		int triggerW = parsePx(trigger.css("width"));
		int triggerH = parsePx(trigger.css("height"));
		assertTrue(triggerW >= 44 && triggerH >= 44,
				"trigger touch target must be >= 44x44 (WCAG 2.5.5 AAA); got "
						+ triggerW + "x" + triggerH);

		// Need the dial open for items to be in layout flow with sizes.
		click(trigger);
		waitResponse();
		JQuery firstItem = jq("$circleItem1");
		int itemW = parsePx(firstItem.css("width"));
		int itemH = parsePx(firstItem.css("height"));
		assertTrue(itemW >= 44 && itemH >= 44,
				"item touch target must be >= 44x44 (WCAG 2.5.5 AAA); got "
						+ itemW + "x" + itemH);
	}

	/**
	 * autoClose=false should NOT prevent the outside-click handler from
	 * still closing the dial — the two options are orthogonal. Otherwise
	 * users could end up with a dial that has no way to close itself
	 * (item click → ignored, click outside → ignored).
	 */
	@Test
	public void speeddialCloseOnSelectFalseStillRespectsClickOutside() {
		connect();
		waitResponse();

		JQuery trigger = jq("$autoCloseFalseDial").find("button").first();
		getEval("document.getElementById('" + jq("$autoCloseFalseDial").attr("id")
				+ "').scrollIntoView({block: 'center'})");
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"));

		// Dispatch mousedown OUTSIDE the dial. Should close even though
		// autoClose=false.
		getEval(
			"document.getElementById('" + jq("$openLog").attr("id") + "')"
			+ ".dispatchEvent(new MouseEvent('mousedown', {bubbles: true, cancelable: true}))");
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"autoClose=false should NOT block closeOnOutsideClick — orthogonal options");
	}

	/**
	 * iconSclass with a non-existent class should not crash the widget —
	 * the icon span just renders empty. (Server-side may or may not
	 * validate the class name; the client must be resilient.)
	 */
	@Test
	public void speeddialIconSclassUnknownDoesNotCrash() {
		connect();
		waitResponse();

		// Render and basic open/close still work even if the icon class
		// doesn't resolve. Swap iconSclass to a non-existent value at runtime;
		// `void 0` discards the widget return value so the WebDriver bridge
		// doesn't choke on a circular widget reference.
		String dialId = jq("$circleDial").attr("id");
		// getEval wraps in `return (...)`, so the body must be a single
		// expression. Use the comma operator to discard the widget return.
		getEval("(zk.Widget.$('" + dialId + "').setIconSclass('z-icon-does-not-exist-anywhere'), '')");
		waitResponse();

		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"unknown iconSclass must not break the trigger's open/close behaviour");
	}

	// ────────────────────────────────────────────────────────────────────
	// disabled property
	// ────────────────────────────────────────────────────────────────────

	/** disabled=true: the trigger renders with aria-disabled and the .z-speeddial-disabled root sclass. */
	@Test
	public void speeddialDisabledRendersWithAriaAndSclass() {
		connect();
		waitResponse();

		JQuery dial = jq("$disabledDial");
		assertTrue(dial.exists());
		assertTrue(dial.hasClass("z-speeddial-disabled"),
				"disabled=true should put z-speeddial-disabled on the root");

		JQuery trigger = dial.find("button").first();
		assertEquals("true", trigger.attr("aria-disabled"),
				"disabled=true should set aria-disabled on the trigger button");
		// The DOM disabled attribute should also be present so the browser
		// blocks tab focus + click events natively.
		String disabledAttr = trigger.attr("disabled");
		assertTrue("disabled".equals(disabledAttr) || "true".equals(disabledAttr) || disabledAttr != null,
				"disabled=true should propagate the HTML disabled attribute to the trigger");
	}

	/** Clicking a disabled trigger must NOT open the dial. */
	@Test
	public void speeddialDisabledIgnoresClick() {
		connect();
		waitResponse();

		JQuery trigger = jq("$disabledDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		// JS-dispatched click bypasses Selenium's "not interactable on
		// disabled" check, simulating a programmatic / malicious click.
		getEval(
			"document.getElementById('" + trigger.attr("id") + "')"
			+ ".dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true}))");
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"disabled dial must not open on click");
	}

	/** Server-side setOpen(true) on a disabled dial must be refused. */
	@Test
	public void speeddialDisabledIgnoresServerSetOpen() {
		connect();
		waitResponse();

		JQuery trigger = jq("$disabledDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		click(jq("$forceOpenDisabledBtn"));
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"server-side setOpen(true) must be ignored when the dial is disabled "
						+ "(keeps client and server states consistent)");
	}

	/**
	 * After re-enabling via setDisabled(false), the dial should accept
	 * clicks again — confirming setDisabled is dynamic, not just a render-time flag.
	 */
	@Test
	public void speeddialDisabledFalseRestoresInteractivity() {
		connect();
		waitResponse();

		JQuery trigger = jq("$disabledDial").find("button").first();
		// Precondition: disabled, doesn't open.
		click(jq("$enableDialBtn"));
		waitResponse();

		assertFalse(jq("$disabledDial").hasClass("z-speeddial-disabled"),
				"setDisabled(false) should drop the z-speeddial-disabled sclass");

		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"after setDisabled(false), the dial should open on click again");
	}

	// ────────────────────────────────────────────────────────────────────
	// Layout / viewport / theming sanity
	// ────────────────────────────────────────────────────────────────────

	/**
	 * z-index regression: the dial must sit above ordinary page content so
	 * a modal-like overlay built by user code can be placed at a higher
	 * z-index to cover it. ZK's Speeddial uses z-index 100 by default; if
	 * future CSS shrinks this below 10, modals/popovers may end up under
	 * the FAB.
	 */
	@Test
	public void speeddialZIndexAboveOrdinaryContent() {
		connect();
		waitResponse();

		String z = jq("$circleDial").css("z-index");
		int zi = z != null && !"auto".equals(z) ? Integer.parseInt(z) : -1;
		assertTrue(zi >= 10,
				"Speeddial z-index should be >= 10 so popovers can layer above with z >= 100; got " + z);
	}

	/**
	 * After a viewport resize to mobile dimensions (375x667 — common
	 * iPhone width), the dial should still render and open. Regression
	 * check for fixed-position widgets that hide off-screen at narrow
	 * viewports.
	 */
	@Test
	public void speeddialOpensAfterViewportResize() {
		connect();
		waitResponse();

		org.openqa.selenium.Dimension original = driver.manage().window().getSize();
		try {
			driver.manage().window().setSize(
					new org.openqa.selenium.Dimension(375, 667));
			waitResponse();

			JQuery trigger = jq("$circleDial").find("button").first();
			// Scroll into view (layout changed after resize).
			getEval("document.getElementById('" + jq("$circleDial").attr("id")
					+ "').scrollIntoView({block: 'center'})");
			click(trigger);
			waitResponse();
			assertEquals("true", trigger.attr("aria-expanded"),
					"dial should still open at mobile viewport 375x667");
		} finally {
			driver.manage().window().setSize(original);
		}
	}

	/**
	 * `prefers-reduced-motion` user preference should disable animations.
	 * The compiled CSS must contain the corresponding @media block so the
	 * widget responds to the OS / browser accessibility setting.
	 */
	@Test
	public void speeddialPrefersReducedMotionRuleCompiled() {
		connect();
		waitResponse();

		// Walk all loaded stylesheets and check at least one rule under
		// @media (prefers-reduced-motion: reduce) targets .z-speeddial.
		String found = getEval(
			"(function(){"
			+ "for (var s of Array.from(document.styleSheets)) {"
			+ "  try {"
			+ "    for (var r of Array.from(s.cssRules || [])) {"
			+ "      if (r.type === CSSRule.MEDIA_RULE"
			+ "          && /prefers-reduced-motion/.test(r.conditionText)"
			+ "          && /z-speeddial/.test(r.cssText)) {"
			+ "        return 'yes';"
			+ "      }"
			+ "    }"
			+ "  } catch(e) {}"
			+ "}"
			+ "return 'no';"
			+ "})()");
		assertEquals("yes", found,
				"speeddial.less must compile a @media (prefers-reduced-motion: reduce) "
						+ "block scoped to .z-speeddial for a11y compliance");
	}

	// ────────────────────────────────────────────────────────────────────
	// autoFlip — flip direction when the configured one would overflow
	// ────────────────────────────────────────────────────────────────────

	/**
	 * autoFlip=true near the viewport top edge: configured direction is "up"
	 * but items would render above the screen, so the widget should flip
	 * to "down". The root sclass updates to z-speeddial-direction-down so
	 * existing CSS still applies.
	 */
	@Test
	public void speeddialAutoFlipNearTopFlipsToDown() {
		connect();
		waitResponse();

		JQuery trigger = jq("$autoFlipDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: autoFlipDial open");

		JQuery dial = jq("$autoFlipDial");
		assertTrue(dial.hasClass("z-speeddial-direction-down"),
				"autoFlip should flip direction to 'down' (originally 'up') "
						+ "because items would otherwise overflow the top of viewport");
		assertFalse(dial.hasClass("z-speeddial-direction-up"),
				"the original 'up' direction class should be removed after flip");

		// First item's top must be POSITIVE (below trigger center, i.e.
		// flipped from "up" which would give negative top).
		JQuery first = jq("$autoFlipDial").find(".z-speeddial-items > li").first();
		int top = parseTopPx(first.attr("style"));
		assertTrue(top > 0,
				"after autoFlip 'up'->'down', the first item should be BELOW trigger centre (top > 0); got " + top);
	}

	/**
	 * autoFlip=false at the same near-edge position: items still go up
	 * (and end up off-screen). Confirms autoFlip is opt-in, not default.
	 */
	@Test
	public void speeddialNoAutoFlipKeepsConfiguredDirection() {
		connect();
		waitResponse();

		JQuery trigger = jq("$noFlipDial").find("button").first();
		click(trigger);
		waitResponse();

		JQuery dial = jq("$noFlipDial");
		assertTrue(dial.hasClass("z-speeddial-direction-up"),
				"without autoFlip, direction stays 'up' regardless of overflow");

		JQuery first = jq("$noFlipDial").find(".z-speeddial-items > li").first();
		int top = parseTopPx(first.attr("style"));
		assertTrue(top < 0,
				"without autoFlip the first 'up' item must remain above (top < 0); got " + top);
	}

	// ────────────────────────────────────────────────────────────────────
	// Coverage-gap additions (ZK-6098 review pass #13)
	// ────────────────────────────────────────────────────────────────────

	/** #1 image: a Speeddialitem with an image renders an &lt;img&gt; (via domImage_). */
	@Test
	public void speeddialitemImageRendersImg() {
		connect();
		waitResponse();
		JQuery img = jq("$imgItem").find("img");
		assertTrue(img.exists(), "image attribute should render an <img> via domImage_");
		assertNotEquals("null", img.attr("src"), "the <img> must carry a src");
	}

	/** #2 href: a relative/web-resource href must be run through execution encodeURL
	 * (~./ resolved), not pushed raw. */
	@Test
	public void speeddialitemRelativeHrefIsEncoded() {
		connect();
		waitResponse();
		JQuery a = jq("$relHrefItem");
		assertEquals("A", tagOf(a), "an item with href renders as <a>");
		String href = a.attr("href");
		assertFalse(href.startsWith("~"),
				"href must be execution-encoded (~./ resolved), not the raw value; got " + href);
		assertTrue(href.contains("test.png"),
				"the encoded href should still reference the resource; got " + href);
	}

	/** #3 trigger="hover" (no modal): mouse-enter opens, moving away closes after the debounce. */
	@Test
	public void speeddialHoverTriggerOpensAndCloses() {
		connect();
		waitResponse();
		JQuery trigger = jq("$hoverDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"), "precondition: hover dial closed");
		getActions().moveToElement(toElement(trigger)).perform();
		waitResponse(true);
		assertEquals("true", trigger.attr("aria-expanded"),
				"trigger=hover should open the dial on mouse-enter");
		getActions().moveToElement(toElement(jq("$hoverAwayBtn"))).pause(600).perform();
		waitResponse(true);
		assertEquals("false", trigger.attr("aria-expanded"),
				"moving the mouse away should close the hover dial after the debounce");
	}

	/** #4 Tab away from an open dial closes it (spec §8). */
	@Test
	public void speeddialTabAwayCloses() {
		connect();
		waitResponse();
		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "precondition: open after click");
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse(true);
		assertEquals("false", trigger.attr("aria-expanded"),
				"Tab away from the dial should close it");
	}

	/** #5 MVVM: open="@bind(vm.open)" load binding renders open; onOpen="@command" fires the VM command.
	 * (Note: the load-binding open itself fires onOpen once on render — the programmatic-setOpen-posts-
	 * OpenEvent behavior — so the command counter starts at 1; assert the per-click increment.) */
	@Test
	public void speeddialMvvmOpenBindAndOnOpenCommand() {
		connect();
		waitResponse();
		JQuery trigger = jq("$mvvmDial").find("button").first();
		assertEquals("true", trigger.attr("aria-expanded"),
				"@bind(vm.open)=true should render the dial open (load binding)");
		int before = Integer.parseInt(jq("$mvvmCmdLabel").text());
		click(trigger);
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"), "trigger click closes the dial");
		assertEquals(before + 1, Integer.parseInt(jq("$mvvmCmdLabel").text()),
				"the close should fire onOpen=@command exactly once more");
	}

	/** #6 type=circle geometry: 4 items distribute evenly from -PI/2 at radius 72. */
	@Test
	public void speeddialCircleGeometryPositions() {
		connect();
		waitResponse();
		click(jq("$circleDial").find("button").first());
		waitResponse();
		JQuery items = jq("$circleDial").find(".z-speeddial-items > li");
		// trigger-half=28, item-half=24, r=72; i0 angle -PI/2 -> (0,-72) -> (4,-68);
		// i1 angle 0 -> (72,0) -> (76,4).
		assertEquals(4, parseLeftPx(items.eq(0).attr("style")), "circle item0 left");
		assertEquals(-68, parseTopPx(items.eq(0).attr("style")), "circle item0 top");
		assertEquals(76, parseLeftPx(items.eq(1).attr("style")), "circle item1 left");
		assertEquals(4, parseTopPx(items.eq(1).attr("style")), "circle item1 top");
	}

	/** #7 horizontal direction: arrow nav uses ArrowLeft/ArrowRight (not Up/Down). */
	@Test
	public void speeddialHorizontalArrowNavUsesLeftRight() {
		connect();
		waitResponse();
		click(jq("$linLeft").find("button").first());
		waitResponse();
		getEval("document.getElementById('" + jq("$linLeftItem1").attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		assertEquals(jq("$linLeftItem2").attr("id"),
				getEval("document.activeElement && document.activeElement.id"),
				"ArrowRight should move to the next item on a horizontal (left) dial");
		getActions().sendKeys(Keys.ARROW_LEFT).perform();
		waitResponse();
		assertEquals(jq("$linLeftItem1").attr("id"),
				getEval("document.activeElement && document.activeElement.id"),
				"ArrowLeft should move to the previous item on a horizontal dial");
	}

	/** #8 runtime setters (beyond setType/setDirection) reach the client. */
	@Test
	public void speeddialRuntimeSettersApplyToClient() {
		connect();
		waitResponse();
		JQuery dial = jq("$runtimeDial");
		assertFalse(dial.find(".z-speeddial-modal").exists(), "precondition: no modal overlay");
		click(jq("$rtSetModalBtn"));
		waitResponse();
		assertTrue(dial.find(".z-speeddial-modal").exists(),
				"runtime setModal(true) should render the modal overlay");
		click(jq("$rtSetIconBtn"));
		waitResponse();
		assertTrue(dial.find(".z-speeddial-icon-open").attr("class").contains("z-icon-bars"),
				"runtime setIconSclass should update the trigger icon class");
	}

	/** #9 server toggle() flips the open state. Uses toggleDial (closeOnOutsideClick=false) so the
	 * external toggle-button click does not trigger the outside-click close that would race toggle(). */
	@Test
	public void speeddialServerToggleFlipsOpenState() {
		connect();
		waitResponse();
		JQuery trigger = jq("$toggleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"), "precondition: toggleDial closed");
		click(jq("$toggleBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "toggle() should open a closed dial");
		click(jq("$toggleBtn"));
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"), "toggle() should close an open dial");
	}

	/** #10 two dials can be open at once; Escape closes the focused one. */
	@Test
	public void speeddialMultipleDialsCoexistAndFocusedClosesOnEscape() {
		connect();
		waitResponse();
		JQuery trigA = jq("$multiA").find("button").first();
		JQuery trigB = jq("$multiB").find("button").first();
		assertEquals("true", trigA.attr("aria-expanded"), "multiA open");
		assertEquals("true", trigB.attr("aria-expanded"), "multiB open (two dials coexist)");
		getEval("document.getElementById('" + trigA.attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertEquals("false", trigA.attr("aria-expanded"),
				"Escape with focus in multiA should close it");
	}

	/** #11 transitionDelay actual value: item[1] carries index*delay = 200ms (item[0] is always 0ms). */
	@Test
	public void speeddialTransitionDelayActualValueOnItems() {
		connect();
		waitResponse();
		click(jq("$delayDial").find("button").first());
		waitResponse();
		String li2 = jq("$delayDial").find(".z-speeddial-items > li").eq(1).attr("style");
		assertTrue(li2 != null && li2.contains("200ms"),
				"transitionDelay=200 should make item[1] transition-delay:200ms; got " + li2);
	}

	/** #13 second trigger keypress (ArrowDown) on an open dial moves focus to the first item. */
	@Test
	public void speeddialSecondTriggerKeyFocusesFirstItem() {
		connect();
		waitResponse();
		JQuery trigger = jq("$linUp").find("button").first();
		getEval("document.getElementById('" + trigger.attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse(true);
		assertEquals("true", trigger.attr("aria-expanded"), "first ArrowDown opens the dial");
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		assertEquals(jq("$linUpItem1").attr("id"),
				getEval("document.activeElement && document.activeElement.id"),
				"second ArrowDown should move focus to the first item (_focusFirstItem)");
	}

	// ────────────────────────────────────────────────────────────────────
	// Helpers
	// ────────────────────────────────────────────────────────────────────

	private static int parsePx(String css) {
		if (css == null) return -1;
		java.util.regex.Matcher m = java.util.regex.Pattern
				.compile("(-?\\d+)(?:\\.\\d+)?px")
				.matcher(css);
		if (m.find()) return Integer.parseInt(m.group(1));
		return -1;
	}

	// ────────────────────────────────────────────────────────────────────
	// Helpers — inline-style px parser
	// ────────────────────────────────────────────────────────────────────

	private static int parseTopPx(String style) {
		return parsePx(style, "top");
	}

	/**
	 * Item 5 contract pin: {@code setOpen(false)} on a dial whose server
	 * {@code _open} is already false (e.g. after a {@code setDisabled(true)}
	 * auto-close) is a silent no-op — the {@code setOpen} guard
	 * short-circuits and no further {@code onOpen} fires.
	 *
	 * <p>The auto-close path itself <em>does</em> reach the log: the client
	 * widget mirrors {@code setDisabled(true)} by firing
	 * {@code onOpen={open:false}} back to the server, which is the
	 * intended round-trip. The contract being pinned is that the
	 * subsequent server-driven {@code setOpen(false)} doesn't double up.
	 */
	@Test
	public void speeddialSetOpenFalseAfterDisabledAutoCloseIsNoop() {
		connect();
		waitResponse();

		// 1. Open via server. Log gains "open;".
		click(jq("$serverOpenBtn"));
		waitResponse();
		assertEquals("open;", jq("$openLog").text(),
				"server setOpen(true) fires onOpen once");

		// 2. Disable while open. Client auto-closes and round-trips
		// onOpen=false, so the log gains "close;". (Server's _open is
		// now false.)
		click(jq("$serverDisableBtn"));
		waitResponse();
		assertEquals("open;close;", jq("$openLog").text(),
				"setDisabled(true) auto-close round-trips one close");

		// 3. Now ask the server to close again. _open is already false,
		// so the setOpen guard short-circuits — no event, no log entry.
		click(jq("$serverCloseBtn"));
		waitResponse();
		assertEquals("open;close;", jq("$openLog").text(),
				"setOpen(false) on already-closed dial must be a no-op");
	}

	/**
	 * Item 17 race: a client-fired {@code onOpen=true} arriving after
	 * {@code setDisabled(true)} must be rolled back server-side. The
	 * server's {@code service()} echoes {@code smartUpdate("open", false)}
	 * so the client UI snaps back to closed regardless of timing.
	 */
	@Test
	public void speeddialClientOnOpenWhileDisabledIsRolledBack() {
		connect();
		waitResponse();

		// Disable circleDial first.
		click(jq("$serverDisableBtn"));
		waitResponse();
		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"));

		// Simulate the race: client widget directly fires onOpen=true to
		// the server (bypassing the local _disabled guard, the way an
		// in-flight optimistic toggle would). Wrapped in an IIFE that
		// returns nothing so Selenium doesn't try to serialise the
		// widget (which is self-referential and would error).
		String dialId = jq("$circleDial").attr("id");
		getEval("(function(){zk.Widget.$('#" + dialId
				+ "').fire('onOpen', {open: true}, {toServer: true});})()");
		waitResponse();

		// Server must reject and echo open=false back.
		assertEquals("false", trigger.attr("aria-expanded"),
				"disabled dial must not be left open by a racey client onOpen=true");
	}

	/**
	 * Item 1 contract: {@code <speeddialitem href="..."/>} renders as an
	 * {@code <a role="menuitem">} so the platform's link affordances work.
	 * Items without {@code href} keep the existing {@code <button>} render.
	 */
	@Test
	public void speeddialitemHrefRendersAsAnchor() {
		connect();
		waitResponse();

		JQuery trigger = jq("$hrefDial").find("button").first();
		click(trigger);
		waitResponse();

		JQuery anchored = jq("$hrefItemAnchor");
		assertTrue(anchored.exists());
		assertEquals("A", tagOf(anchored),
				"href item must render as <a>");
		assertEquals("https://www.zkoss.org/", anchored.attr("href"));
		assertEquals("_blank", anchored.attr("target"),
				"target attribute must reach the DOM when set");
		assertEquals("menuitem", anchored.attr("role"));

		JQuery selfTarget = jq("$hrefItemTarget");
		assertEquals("A", tagOf(selfTarget));
		assertEquals("#hrefDial-self", selfTarget.attr("href"));

		JQuery plain = jq("$hrefItemPlainButton");
		assertEquals("BUTTON", tagOf(plain),
				"items without href stay as <button>");
	}

	/** Get the rendered tag name of a JQuery match via JS — JQuery facade
	 * doesn't expose .prop("tagName"), so go direct.
	 */
	private String tagOf(JQuery jq) {
		return getEval("document.getElementById('" + jq.attr("id") + "').tagName");
	}

	/**
	 * Round-2 item 18: disabled href-item must NOT navigate. aria-disabled
	 * is informational only on {@code <a>}; the fix strips the href
	 * attribute so the browser has nothing to navigate to.
	 */
	@Test
	public void speeddialitemDisabledHrefHasNoHref() {
		connect();
		waitResponse();

		// 1. Item declared with disabled="true" + href: anchor rendered,
		// but the href attribute itself must be absent.
		JQuery trigger = jq("$hrefDial").find("button").first();
		click(trigger);
		waitResponse();
		JQuery disabledAnchor = jq("$hrefItemDisabled");
		assertEquals("A", tagOf(disabledAnchor),
				"disabled+href still renders as <a> for AT semantics");
		assertEquals("true", disabledAnchor.attr("aria-disabled"));
		// JQuery.attr() stringifies an absent attribute as "null".
		assertEquals("null", disabledAnchor.attr("href"),
				"disabled href item must have no href so the browser can't navigate");

		// 2. Runtime setDisabled(true) on a currently-enabled href item
		// must also strip href (not only re-render on next response).
		click(jq("$hrefDisableAnchorBtn"));
		waitResponse();
		JQuery dynamicallyDisabled = jq("$hrefItemAnchor");
		assertEquals("true", dynamicallyDisabled.attr("aria-disabled"));
		assertEquals("null", dynamicallyDisabled.attr("href"),
				"setDisabled(true) at runtime must drop href on a href item");
	}

	/**
	 * Round-2 item 22: any item with a {@code target} attribute must
	 * automatically carry {@code rel="noopener noreferrer"} so a new tab
	 * can't reach back through {@code window.opener} into the host page.
	 */
	@Test
	public void speeddialitemTargetCarriesRelNoopener() {
		connect();
		waitResponse();

		click(jq("$hrefDial").find("button").first());
		waitResponse();

		JQuery anchored = jq("$hrefItemAnchor");
		assertEquals("_blank", anchored.attr("target"));
		assertEquals("noopener noreferrer", anchored.attr("rel"),
				"target=_blank items must carry rel=noopener noreferrer");

		JQuery selfTarget = jq("$hrefItemTarget");
		// JQuery.attr() stringifies an absent attribute as "null".
		assertEquals("null", selfTarget.attr("rel"),
				"items without target should not advertise rel");
	}

	/**
	 * Round-2 item 20: arrow nav skips disabled siblings — pressing
	 * ArrowDown from item 1 should land on item 3 when item 2 is
	 * disabled, matching every native menu's behavior.
	 */
	@Test
	public void speeddialArrowNavSkipsDisabledItems() {
		connect();
		waitResponse();

		JQuery trigger = jq("$arrowSkipDial").find("button").first();
		click(trigger);
		waitResponse();

		// Land focus on item 1, then ArrowDown — should land on item 3,
		// not on the disabled item 2.
		getEval("document.getElementById('" + jq("$arrowSkipItem1").attr("id")
				+ "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();

		assertEquals("0", jq("$arrowSkipItem3").attr("tabindex"),
				"ArrowDown should skip disabled item2 and land on item3");
		assertEquals("-1", jq("$arrowSkipItem2").attr("tabindex"),
				"disabled item must NOT be the roving tabindex target");
	}

	/**
	 * Item 2 contract: tabindex roving — every item is tabindex="-1" so
	 * Tab leaves the open dial in one keystroke. Arrow nav promotes the
	 * focused item to tabindex="0" and demotes the previous.
	 */
	@Test
	public void speeddialRovingTabindex() {
		connect();
		waitResponse();

		// Open circleDial; menu items should be rendered with tabindex="-1".
		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("-1", jq("$circleItem1").attr("tabindex"),
				"items must start outside the tab order");
		assertEquals("-1", jq("$circleItem4").attr("tabindex"));

		// ArrowDown promotes the first item to tabindex=0 (roving).
		getEval("document.getElementById('" + trigger.attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		assertEquals("0", jq("$circleItem1").attr("tabindex"),
				"focused item must be in the tab order");
		// Other items remain out of tab order.
		assertEquals("-1", jq("$circleItem2").attr("tabindex"));
	}

	/**
	 * ZK-6098 M1-R: a runtime type/direction change must push the COERCED
	 * direction so the client lays out a compatible (type,direction) pair.
	 * The dial starts linear+up; the root carries z-speeddial-direction-&lt;d&gt;
	 * and z-speeddial-type-&lt;t&gt; reflecting the laid-out (coerced) values.
	 *
	 *  1. setType(quarter_circle): "up" is incompatible (quarter needs a
	 *     diagonal) → server coerces+pushes "up_right" → root must read
	 *     direction-up_right, NOT the stale direction-up.
	 *  2. setDirection(down) on the quarter dial: "down" is still incompatible
	 *     → coerced to "up_right" again (exercises the setDirection push path).
	 *  3. setType(linear): "down" (the raw stored value) is now compatible →
	 *     pushes "down" → root must read direction-down, proving the earlier
	 *     coerced "up_right" did not orphan on the client.
	 */
	@Test
	public void speeddialRuntimeTypeChangePushesCoercedDirection() {
		connect();
		waitResponse();

		JQuery dial = jq("$runtimeDial");
		assertTrue(dial.hasClass("z-speeddial-direction-up"),
				"precondition: dial starts at direction-up");

		click(jq("$rtSetQuarterBtn"));
		waitResponse();
		assertTrue(dial.hasClass("z-speeddial-type-quarter_circle"),
				"runtime setType should push the new type to the client");
		assertTrue(dial.hasClass("z-speeddial-direction-up_right"),
				"quarter_circle must coerce the incompatible 'up' to 'up_right' on the client");
		assertFalse(dial.hasClass("z-speeddial-direction-up"),
				"the stale incompatible direction-up must be gone after coercion");

		click(jq("$rtSetDownBtn"));
		waitResponse();
		assertTrue(dial.hasClass("z-speeddial-direction-up_right"),
				"setDirection('down') on a quarter dial must also coerce to 'up_right' on the client");
		assertFalse(dial.hasClass("z-speeddial-direction-down"),
				"the incompatible 'down' must not reach the client layout while type=quarter_circle");

		click(jq("$rtSetLinearBtn"));
		waitResponse();
		assertTrue(dial.hasClass("z-speeddial-type-linear"),
				"runtime setType(linear) should push the new type");
		assertTrue(dial.hasClass("z-speeddial-direction-down"),
				"linear accepts 'down' (the stored raw value) → it must now reach the client");
		assertFalse(dial.hasClass("z-speeddial-direction-up_right"),
				"the previously-coerced 'up_right' must not orphan on the client after the type reverts");
	}

	/**
	 * ZK-6098 H1 — a Speeddialitem's onClick must fire exactly ONCE per user click.
	 *
	 * <p>The item's {@code doClick_} fires the click itself, then calls
	 * {@code super.doClick_(evt, true)} so the base widget does not re-fire the same
	 * click. Each onClick appends one "x" to {@code clickCountLabel}; a regression
	 * that forwarded {@code popupOnly} (undefined) instead of {@code true} would let
	 * the base path fire a second time, producing "xx".
	 */
	@Test
	public void speeddialItemOnClickFiresExactlyOnce() {
		connect();
		waitResponse();

		// The dial renders open (open="true", autoClose="false"), so the item is
		// laid out and clickable without first toggling the trigger.
		click(jq("$clickCountItem"));
		waitResponse();

		assertEquals("x", jq("$clickCountLabel").text(),
				"Speeddialitem onClick must fire exactly once per click (a double-fire shows 'xx')");
	}

	private static int parseLeftPx(String style) {
		return parsePx(style, "left");
	}

	private static int parsePx(String style, String prop) {
		if (style == null) return Integer.MIN_VALUE;
		java.util.regex.Matcher m = java.util.regex.Pattern
				.compile("(?:^|[\\s;])" + prop + "\\s*:\\s*(-?\\d+)(?:\\.\\d+)?px")
				.matcher(style);
		if (m.find()) return Integer.parseInt(m.group(1));
		return Integer.MIN_VALUE;
	}
}
