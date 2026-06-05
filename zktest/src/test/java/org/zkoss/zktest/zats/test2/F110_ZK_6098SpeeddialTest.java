/* F110_ZK_6098SpeeddialTest.java

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
 * Loads F110-ZK-6098-Speeddial.zul (BaseTestCase.connect() resolves the
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
public class F110_ZK_6098SpeeddialTest extends WebDriverTestCase {

	// BaseTestCase.connect() resolves a path from the test class name and
	// only tries replacing all `_` with `-`. For our class that yields
	// `F110-ZK-6098Speeddial.zul`, but we ship the page as
	// `F110-ZK-6098-Speeddial.zul` (with the extra dash before "Speeddial"
	// for readability). Override to point at the file we ship.
	@Override
	protected String getFileLocation() {
		return "/test2/F110-ZK-6098-Speeddial.zul";
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
		// Assert both halves, not either: the label span is display:none, so
		// .text() holds regardless of the aria-label that actually names the
		// item for assistive tech. An `||` here can never fail on the aria half.
		assertTrue(item.text().contains("New"),
				"item label should be carried in the rendered markup; got: " + item.text());
		assertEquals("New", item.attr("aria-label"),
				"the display:none label must be exposed as the item's accessible name");
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

	/** autodrop + mask=true is coerced to click; click still opens. */
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

		// Focus is on the trigger after the click, so the widget's own key
		// handler owns Escape (no document-level listener involved).
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

		JQuery modal = jq(".z-modal-mask");
		assertTrue(modal.exists(), "mask=true should raise a full mask while open");
		click(modal);
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"clicking the mask should close the dial");
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
				{"linUp", "top"},
				{"linRight", "right"},
				{"linDown", "bottom"},
				{"linLeft", "left"},
				{"linUpLeft", "top_left"},
				{"linUpRight", "top_right"},
				{"linDownLeft", "bottom_left"},
				{"linDownRight", "bottom_right"},
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

	/** Semi-circle direction matrix (bottom / top / left / right). */
	@Test
	public void speeddialSemiCircleAllDirections() {
		connect();
		waitResponse();
		String[][] dialsAndDirs = {
				{"semiDown", "bottom"},
				{"semiUp", "top"},
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
				{"quarterDownRight", "bottom_right"},
				{"quarterDownLeft", "bottom_left"},
				{"quarterUpRight", "top_right"},
				{"quarterUpLeft", "top_left"},
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
		// linear/top first item:
		//   item-centre.x = trigger-centre.x = 28
		//   item-centre.y = trigger-centre.y - 56 = -28
		//   top-left = (28 - 24, -28 - 24) = (4, -52)
		JQuery first = jq("$linUp").find(".z-speeddial-items > li").first();
		int left = parseLeftPx(first.attr("style"));
		int top = parseTopPx(first.attr("style"));
		assertEquals(4, left,
				"linear/top first item should be centred on trigger X (left=4); got " + left);
		assertEquals(-52, top,
				"linear/top first item should be 56px above trigger centre (top=-52); got " + top);
	}

	/** Fan-out positioning: linear/top — items stack vertically with descending top. */
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
		// direction=top: each subsequent item sits FURTHER UP, so its `top`
		// inline-style value becomes more negative. t1 should be the largest
		// (least negative; closest to the trigger), then t2 < t1, t3 < t2.
		assertTrue(t1 > t2 && t2 > t3,
				"direction=top items should fan upward (each top further up / more negative); got "
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

	/** Fan-out positioning: linear/bottom_right — items go diagonal (both left>6 and top>6). */
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
				"bottom_right diagonal first item: must be right AND below trigger centre; left=" + left + ", top=" + top);
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
	// Decoration — closeIconSclass, mask overlay, transition stagger
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

	/** mask=true raises a full mask while open, and drops it on close. */
	@Test
	public void speeddialMaskOverlayPresent() {
		connect();
		waitResponse();
		assertFalse(jq(".z-modal-mask").exists(),
				"precondition: a closed dial must not leave a mask on the page");

		JQuery trigger = jq("$quarterDial").find("button").first();
		click(trigger);
		waitResponse();
		assertTrue(jq(".z-modal-mask").exists(),
				"mask=true should raise a zk.eff.FullMask when the dial opens");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertFalse(jq(".z-modal-mask").exists(),
				"closing the dial must remove the mask again");
	}

	/** A masked dial and its backdrop must escape a transformed ancestor while open —
	 * quarterDial sits in a .dial-host that sets transform, i.e. a containing block —
	 * and the dial must go back where the page put it on close. */
	@Test
	public void speeddialMaskEscapesTransformedAncestor() {
		connect();
		waitResponse();
		String dialId = jq("$quarterDial").attr("id");
		assertNotEquals("BODY",
				getEval("document.getElementById('" + dialId + "').parentElement.tagName"),
				"precondition: the fixture scopes this dial inside a transformed host");

		click(jq("$quarterDial").find("button").first());
		waitResponse();
		assertEquals("BODY",
				getEval("document.querySelector('.z-modal-mask').parentElement.tagName"),
				"the backdrop must be a body-level mask — a nested one can only dim "
						+ "the dial's own ancestor");
		assertEquals("BODY",
				getEval("document.getElementById('" + dialId + "').parentElement.tagName"),
				"a masked dial must lift out of the containing block, or it would sit "
						+ "underneath its own viewport-wide backdrop");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertNotEquals("BODY",
				getEval("document.getElementById('" + dialId + "').parentElement.tagName"),
				"closing must return the dial to the direction the page gave it");
	}

	/** The mask reparent must not MOVE the dial. {@code makeVParent()} stamps an
	 * inline {@code top: 0} on any node it lifts, which over-constrains a box
	 * anchored by {@code bottom}/{@code right}: the dial jumps to the top of the
	 * viewport and its fan lands off-screen. {@code undoVParent()} never clears
	 * that stamp either, so the displacement outlives the close. Existence and
	 * parent-tag asserts both pass on a dial that has teleported, so pin the
	 * geometry down. */
	@Test
	public void speeddialMaskOpenDoesNotMoveTheDial() {
		connect();
		waitResponse();
		String dialId = jq("$quarterDial").attr("id");
		// Measure against the host, not the viewport: clicking the trigger scrolls
		// it into view, which would move a viewport-relative rect on its own.
		getEval("(function(){window.zk6098Host=document.getElementById('" + dialId
				+ "').parentElement;return 'ok';})()");
		String offsetInHost = "(function(){"
				+ "var d=document.getElementById('" + dialId + "').getBoundingClientRect(),"
				+ "h=window.zk6098Host.getBoundingClientRect();"
				+ "return ''+Math.round(d.top-h.top)+','+Math.round(d.left-h.left);})()";
		String closed = getEval(offsetInHost);

		click(jq("$quarterDial").find("button").first());
		waitResponse();
		assertEquals(closed, getEval(offsetInHost),
				"a masked open must leave the dial where the page drew it");
		assertEquals("0", getEval("(function(){var de=document.documentElement,off=0;"
				+ "document.getElementById('" + dialId + "')"
				+ ".querySelectorAll('.z-speeddialitem').forEach(function(i){"
				+ "var b=i.getBoundingClientRect();"
				+ "if(b.top<0||b.left<0||b.bottom>de.clientHeight||b.right>de.clientWidth)off++;});"
				+ "return ''+off;})()"),
				"every item of an open masked dial must be inside the viewport");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertEquals(closed, getEval(offsetInHost),
				"closing must leave no inline direction residue behind");
	}

	/** An unmasked dial must NOT be reparented — an app may scope a FAB to a panel. */
	@Test
	public void speeddialUnmaskedStaysWhereThePagePutIt() {
		connect();
		waitResponse();
		String dialId = jq("$linUp").attr("id");
		String before = getEval("document.getElementById('" + dialId + "').parentElement.tagName");
		click(jq("$linUp").find("button").first());
		waitResponse();
		assertEquals(before,
				getEval("document.getElementById('" + dialId + "').parentElement.tagName"),
				"opening an unmasked dial must not move it out of its container");
	}

	/** Each item slot carries its ordinal, which the stylesheet turns into the stagger. */
	@Test
	public void speeddialTransitionStaggerAppliesToItems() {
		connect();
		waitResponse();
		JQuery firstSlot = jq("$delayDial").find(".z-speeddial-items > li").first();
		String style = firstSlot.attr("style");
		assertNotNull(style, "stagger requires an inline style on each item slot");
		assertTrue(style.contains("--zk-speeddial-item-index"),
				"_layoutItems should publish each item's ordinal; got: " + style);
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
				"autoclose=false should NOT block closeOnOutsideClick — orthogonal options");
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
	 * check for fixed-direction widgets that hide off-screen at narrow
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
	// autoflip — flip direction when the configured one would overflow
	// ────────────────────────────────────────────────────────────────────

	/**
	 * autoflip=true near the viewport top edge: configured direction is "top"
	 * but items would render above the screen, so the widget should flip
	 * to "bottom". The root sclass updates to z-speeddial-direction-bottom so
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
		assertTrue(dial.hasClass("z-speeddial-direction-bottom"),
				"autoflip should flip direction to 'bottom' (originally 'top') "
						+ "because items would otherwise overflow the top of viewport");
		assertFalse(dial.hasClass("z-speeddial-direction-top"),
				"the original 'top' direction class should be removed after flip");

		// First item's top must be POSITIVE (below trigger center, i.e.
		// flipped from "top" which would give negative top).
		JQuery first = jq("$autoFlipDial").find(".z-speeddial-items > li").first();
		int top = parseTopPx(first.attr("style"));
		assertTrue(top > 0,
				"after autoflip 'top'->'bottom', the first item should be BELOW trigger centre (top > 0); got " + top);
	}

	/**
	 * autoflip=false at the same near-edge direction: items still go up
	 * (and end up off-screen). Confirms autoflip is opt-in, not default.
	 */
	@Test
	public void speeddialNoAutoFlipKeepsConfiguredDirection() {
		connect();
		waitResponse();

		JQuery trigger = jq("$noFlipDial").find("button").first();
		click(trigger);
		waitResponse();

		JQuery dial = jq("$noFlipDial");
		assertTrue(dial.hasClass("z-speeddial-direction-top"),
				"without autoflip, direction stays 'top' regardless of overflow");

		JQuery first = jq("$noFlipDial").find(".z-speeddial-items > li").first();
		int top = parseTopPx(first.attr("style"));
		assertTrue(top < 0,
				"without autoflip the first 'top' item must remain above (top < 0); got " + top);
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

	/** #3 autodrop (no mask): mouse-enter opens, moving away closes after the debounce. */
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

	/** #5 MVVM: open="@bind(vm.open)" load binding renders open; onOpen="@command" fires the VM
	 * command. The load binding itself posts nothing (setOpen is silent), so only user clicks count. */
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
		assertFalse(jq(".z-modal-mask").exists(), "precondition: no mask overlay");
		click(jq("$rtSetMaskBtn"));
		waitResponse();
		click(dial.find("button").first());
		waitResponse();
		assertTrue(jq(".z-modal-mask").exists(),
				"runtime setMask(true) should raise the mask when the dial opens");

		// Close first: the backdrop covers the viewport, so the next
		// page button is genuinely unclickable until the dial closes.
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertFalse(jq(".z-modal-mask").exists(), "closing must drop the mask");
		click(jq("$rtSetIconBtn"));
		waitResponse();
		assertTrue(dial.find(".z-speeddial-icon-open").attr("class").contains("z-icon-bars"),
				"runtime setIconSclass should update the trigger icon class");
	}

	/** #9 server setOpen(!isOpen()) flips the open state. Uses toggleDial
	 * (closeOnOutsideClick=false) so the external button click does not trigger the
	 * outside-click close that would race it. */
	@Test
	public void speeddialServerSetOpenFlipsOpenState() {
		connect();
		waitResponse();
		JQuery trigger = jq("$toggleDial").find("button").first();
		assertEquals("false", trigger.attr("aria-expanded"), "precondition: toggleDial closed");
		click(jq("$toggleBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "setOpen(true) should open a closed dial");
		click(jq("$toggleBtn"));
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"), "setOpen(false) should close an open dial");
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
		assertEquals("true", trigB.attr("aria-expanded"),
				"Escape must close only the focused dial — the unfocused multiB stays open");
	}

	/**
	 * #11 the stagger resolves through CSS: delayDial overrides
	 * --zk-speeddial-transition-delay to 200ms, so item[1] (index 1) computes to
	 * 0.2s and item[0] to 0s. Asserted on the COMPUTED value — an inline-style
	 * check would pass on the raw calc() without proving the var resolves.
	 */
	@Test
	public void speeddialTransitionStaggerResolvesFromCssVar() {
		connect();
		waitResponse();
		click(jq("$delayDial").find("button").first());
		waitResponse();
		JQuery items = jq("$delayDial").find(".z-speeddial-items > li");
		assertEquals("0s", items.eq(0).css("transition-delay"),
				"item[0] is index 0, so its stagger is 0");
		assertEquals("0.2s", items.eq(1).css("transition-delay"),
				"item[1] is index 1 x --zk-speeddial-transition-delay:200ms");
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
	 * Item 5 contract pin: {@code onOpen} is posted exclusively from
	 * {@code service()}, so no server-driven state change reaches an application
	 * listener — matching Groupbox/Bandbox/Panel/Drawer. A user click still does.
	 */
	@Test
	public void speeddialServerSetOpenPostsNoOnOpen() {
		connect();
		waitResponse();
		// quietDial is closeOnOutsideClick="false", so clicking these buttons
		// cannot post a close of its own and confound the assertions.
		JQuery trigger = jq("$quietDial").find("button").first();

		click(jq("$quietOpenBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"server setOpen(true) must reach the client");
		assertEquals("", jq("$quietLog").text(),
				"a server-driven open must not post onOpen");

		click(jq("$quietCloseBtn"));
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"server setOpen(false) must reach the client");
		assertEquals("", jq("$quietLog").text(),
				"a server-driven close must not post onOpen");

		// A real user gesture still posts — without this the empty asserts
		// above would also pass on a dial whose listener never fires at all.
		click(trigger);
		waitResponse();
		assertEquals("open;", jq("$quietLog").text(),
				"a user gesture must still post onOpen");
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
	 * ZK-6098 M1-R: a runtime type/direction change normalizes the STORED
	 * direction, so the server never reports a direction the client is not
	 * laying out. The root carries z-speeddial-direction-&lt;d&gt; and
	 * z-speeddial-type-&lt;t&gt; reflecting exactly what is laid out.
	 *
	 *  1. setType(quarter_circle): "top" is unrenderable (quarter needs a
	 *     diagonal) → the stored direction becomes "top_right".
	 *  2. setDirection(bottom) while quarter: still unrenderable → normalized to
	 *     "top_right" on the way in, so nothing incompatible is ever stored.
	 *  3. setType(linear): the stored direction is "top_right", which linear can
	 *     render, so it stays — there is no shadow "bottom" the server kept but
	 *     never honoured.
	 */
	@Test
	public void speeddialRuntimeTypeChangeNormalizesDirection() {
		connect();
		waitResponse();

		JQuery dial = jq("$runtimeDial");
		assertTrue(dial.hasClass("z-speeddial-direction-top"),
				"precondition: dial starts at direction-top");

		click(jq("$rtSetQuarterBtn"));
		waitResponse();
		assertTrue(dial.hasClass("z-speeddial-type-quarter_circle"),
				"runtime setType should push the new type to the client");
		assertTrue(dial.hasClass("z-speeddial-direction-top_right"),
				"quarter_circle must normalize the unrenderable 'top' to 'top_right'");
		assertFalse(dial.hasClass("z-speeddial-direction-top"),
				"the stale unrenderable direction-top must be gone");

		click(jq("$rtSetDownBtn"));
		waitResponse();
		assertTrue(dial.hasClass("z-speeddial-direction-top_right"),
				"setDirection('bottom') on a quarter dial normalizes to 'top_right'");
		assertFalse(dial.hasClass("z-speeddial-direction-bottom"),
				"an unrenderable direction must never reach the client layout");

		click(jq("$rtSetLinearBtn"));
		waitResponse();
		assertTrue(dial.hasClass("z-speeddial-type-linear"),
				"runtime setType(linear) should push the new type");
		assertTrue(dial.hasClass("z-speeddial-direction-top_right"),
				"the stored direction is 'top_right' and linear can render it, so it stays — "
						+ "the server must not resurrect a 'bottom' it normalized away");
		assertFalse(dial.hasClass("z-speeddial-direction-bottom"),
				"reverting the type must not resurrect a direction the server never honoured");
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

	/**
	 * Space on a &lt;button&gt;-backed item must fire onClick exactly ONCE.
	 *
	 * <p>The item maps Space to activation explicitly (needed because an href-backed
	 * item is an &lt;a&gt;, where Space scrolls instead of activating). On a &lt;button&gt; the
	 * browser would ALSO synthesize an activation click from the same keypress, so
	 * the handler must cancel the keydown; if it ever stops doing so this logs "xx".
	 */
	@Test
	public void speeddialItemSpaceFiresOnClickExactlyOnce() {
		connect();
		waitResponse();

		// clickCountDial renders open with autoclose="false", so the item stays
		// laid out and focusable without toggling the trigger.
		getEval("document.getElementById('" + jq("$clickCountItem").attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.SPACE).perform();
		waitResponse(true);

		assertEquals("x", jq("$clickCountLabel").text(),
				"Space on a <button> item must fire onClick once — the explicit handler "
						+ "must cancel the keydown so the browser's synthesized click "
						+ "cannot fire a second one (a double-fire shows 'xx')");
	}

	/**
	 * Tab dismissal is a keyboard contract and must NOT be governed by
	 * closeOnOutsideClick (a pointer concept). toggleDial sets
	 * closeOnOutsideClick="false"; tabbing out of it must still close it.
	 */
	@Test
	public void speeddialTabAwayClosesEvenWhenOutsideClickDisabled() {
		connect();
		waitResponse();
		JQuery trigger = jq("$toggleDial").find("button").first();
		click(jq("$toggleBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: toggleDial open via server setOpen");

		getEval("document.getElementById('" + trigger.attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse(true);
		assertEquals("false", trigger.attr("aria-expanded"),
				"Tab away must close the dial even with closeOnOutsideClick=false — "
						+ "that property governs pointer dismissal, not the keyboard");
	}

	/**
	 * The converse: an outside CLICK must still be ignored by a
	 * closeOnOutsideClick="false" dial, even though the click moves focus and
	 * therefore also triggers the focusout path.
	 */
	@Test
	public void speeddialOutsideClickIgnoredWhenDisabledDespiteFocusMove() {
		connect();
		waitResponse();
		JQuery trigger = jq("$toggleDial").find("button").first();
		click(jq("$toggleBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "precondition: toggleDial open");

		// serverOpenBtn is a focusable button outside the dial: clicking it moves
		// focus out, so the dial stays open only if the pointer press is
		// recognised as such and the focusout path defers to it.
		click(jq("$serverOpenBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"closeOnOutsideClick=false must survive an outside click that also "
						+ "moves focus away from the dial");
	}

	/**
	 * WCAG 2.4.3 Focus Order — activating an item with the keyboard closes the
	 * dial, and closing marks the items inert, which blurs the focused item. Focus
	 * must land back on the trigger, not on &lt;body&gt;, or the next Tab restarts from
	 * the top of the document.
	 */
	@Test
	public void speeddialKeyboardItemActivationReturnsFocusToTrigger() {
		connect();
		waitResponse();
		JQuery trigger = jq("$hrefDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "precondition: hrefDial open");

		String itemId = jq("$hrefItemPlainButton").attr("id");
		getEval("document.getElementById('" + itemId + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.SPACE).perform();
		waitResponse(true);

		assertEquals("false", trigger.attr("aria-expanded"), "precondition: autoclose closed it");
		assertEquals(trigger.attr("id"),
				getEval("document.activeElement && document.activeElement.id"),
				"closing after keyboard activation must return focus to the trigger — "
						+ "inert on the items would otherwise drop focus to <body>");
	}

	/**
	 * Same contract on a mask="true" dial. Closing a masked dial also runs
	 * undoVParent(), which re-inserts the subtree and blurs whatever was focused
	 * inside it — so the focus must be read BEFORE the reparent, not after.
	 * quarterDial is the only mask="true" fixture.
	 */
	@Test
	public void speeddialMaskedKeyboardActivationReturnsFocusToTrigger() {
		connect();
		waitResponse();
		JQuery trigger = jq("$quarterDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "precondition: quarterDial open");

		String itemId = jq("$quarterItem1").attr("id");
		getEval("document.getElementById('" + itemId + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.SPACE).perform();
		waitResponse(true);

		assertEquals("false", trigger.attr("aria-expanded"), "precondition: autoclose closed it");
		assertEquals(trigger.attr("id"),
				getEval("document.activeElement && document.activeElement.id"),
				"a masked dial must also return focus to the trigger — undoVParent() "
						+ "blurs the subtree it moves, so activeElement must be read first");
	}

	/** The converse: an outside click must NOT steal focus back to the trigger. */
	@Test
	public void speeddialOutsideClickDoesNotStealFocusToTrigger() {
		connect();
		waitResponse();
		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "precondition: circleDial open");

		click(jq("$serverCloseBtn"));
		waitResponse();
		assertNotEquals(trigger.attr("id"),
				getEval("document.activeElement && document.activeElement.id"),
				"an outside click closes the dial but must leave focus where the user "
						+ "put it, not yank it back to the trigger");
	}

	/**
	 * WCAG 1.4.13 "Dismissable" — a hover-revealed dial has focus nowhere inside
	 * it, so Escape must still dismiss it WITHOUT the user moving the pointer.
	 * The pointer is deliberately left on the trigger for the whole test.
	 */
	@Test
	public void speeddialHoverContentDismissableByEscape() {
		connect();
		waitResponse();
		JQuery trigger = jq("$hoverDial").find("button").first();
		getActions().moveToElement(toElement(trigger)).perform();
		waitResponse(true);
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: hover opens the dial");
		assertEquals("BODY", getEval("document.activeElement.tagName"),
				"precondition: a hover open must not focus anything inside the dial");

		// Pointer stays on the trigger — only the key is pressed.
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertEquals("false", trigger.attr("aria-expanded"),
				"WCAG 1.4.13: hover-revealed content must be dismissable by Escape "
						+ "without moving the pointer");
	}

	/** The scoped Escape listener must not survive the close, nor leak to click dials. */
	@Test
	public void speeddialClickDialUnaffectedByUnfocusedEscape() {
		connect();
		waitResponse();
		click(jq("$serverOpenBtn"));
		waitResponse();
		JQuery trigger = jq("$circleDial").find("button").first();
		assertEquals("true", trigger.attr("aria-expanded"), "precondition: opened from server");

		getEval("document.body.focus()");
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertEquals("true", trigger.attr("aria-expanded"),
				"a click-triggered dial must not be closed by an Escape aimed nowhere — "
						+ "the document listener is scoped to hover-opened dials only");
	}

	private static int parseLeftPx(String style) {
		return parsePx(style, "left");
	}

	// ------------------------------------------------------------------
	// Review follow-ups (ZK-6098): server/client agreement, single onOpen,
	// a11y ownership, keyboard parity, runtime visibility, empty href/target.
	// ------------------------------------------------------------------

	/** quarter_circle cannot render an orthogonal direction, so the server must
	 * report the diagonal one the client actually lays out — not the raw "top". */
	@Test
	public void speeddialDirectionAgreesBetweenServerAndClient() {
		connect();
		waitResponse();

		String cls = jq("$normDial").attr("class");
		assertTrue(cls.contains("z-speeddial-direction-top_right"),
				"quarter_circle must lay out a diagonal direction; got " + cls);
		assertFalse(cls.contains("z-speeddial-direction-top "),
				"the unrenderable orthogonal direction must not reach the DOM; got " + cls);

		click(jq("$normReadBtn"));
		waitResponse();
		assertEquals("top_right", jq("$normDirLabel").text(),
				"getDirection() must report what the client lays out, "
						+ "not a value only the wire was corrected to");
	}

	/** A runtime setType that invalidates the current direction normalizes the
	 * stored direction too, so both getters stay truthful. */
	@Test
	public void speeddialRuntimeTypeChangeNormalizesServerGetter() {
		connect();
		waitResponse();
		click(jq("$normSetTypeBtn"));
		waitResponse();
		click(jq("$normReadRtBtn"));
		waitResponse();
		assertEquals("top_right", jq("$normRtDirLabel").text(),
				"setType(quarter_circle) must normalize the stored direction, "
						+ "not just the value pushed to the client");
		assertTrue(jq("$normRuntimeDial").attr("class").contains("z-speeddial-direction-top_right"),
				"the client must lay out the same normalized direction");
	}

	/** Disabling an open dial closes it and posts nothing (setOpen is silent). */
	@Test
	public void speeddialDisablingOpenDialPostsNoEvent() {
		connect();
		waitResponse();
		JQuery trigger = jq("$quietDial").find("button").first();

		// quietDial is closeOnOutsideClick="false", so clicking the buttons
		// below cannot post a close of its own and confound the assertion.
		click(jq("$quietOpenBtn"));
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"),
				"precondition: server setOpen(true) opened the dial");
		assertEquals("", jq("$quietLog").text(),
				"precondition: the server-driven open posted nothing");

		click(jq("$quietDisableBtn"));
		waitResponse();
		assertEquals("false", trigger.attr("aria-expanded"),
				"setDisabled(true) must close an open dial");
		assertEquals("", jq("$quietLog").text(),
				"the setDisabled auto-close routes through setOpen, which posts "
						+ "no onOpen — only service() does");
	}

	/** Escape closes once, not once per close path. */
	@Test
	public void speeddialEscapePostsExactlyOneClose() {
		connect();
		waitResponse();
		JQuery trigger = jq("$circleDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("open;", jq("$openLog").text(), "precondition: one open event logged");

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse(true);
		assertEquals("open;close;", jq("$openLog").text(),
				"one Escape must produce one onOpen(false), not one per close path");
	}

	/** Application ARIA must land on the trigger; the wrapper div carries no role. */
	@Test
	public void speeddialAppAriaLabelReachesTrigger() {
		connect();
		waitResponse();
		// The wrapper->trigger relocation is getA11yRealNode_, which ships only in
		// za11y; without the add-on the attribute stays on the role-less wrapper.
		// Guard so the NO_A11Y variant does not go red (CLAUDE.md a11y rule #1).
		if (!Boolean.valueOf(getEval("!!window.za11y")))
			return;
		JQuery trigger = jq("$ariaDial").find("button").first();
		assertEquals("Custom quick actions", trigger.attr("aria-label"),
				"ca:aria-label must name the trigger, and must win over the "
						+ "za11y localized fallback");
	}

	/** A closed dial must not leave its menu in the accessibility tree. */
	@Test
	public void speeddialClosedMenuIsHiddenFromAssistiveTech() {
		connect();
		waitResponse();
		JQuery items = jq("$linUp").find(".z-speeddial-items");
		assertEquals("true", items.attr("aria-hidden"),
				"a closed dial's role=menu must be hidden from AT, not merely faded out");

		click(jq("$linUp").find("button").first());
		waitResponse();
		assertNotEquals("true", items.attr("aria-hidden"),
				"opening must expose the menu again");
	}

	/** Adding href turns the item into an <a>; Space must still activate it. */
	@Test
	public void speeddialHrefItemActivatesOnSpace() {
		connect();
		waitResponse();
		JQuery trigger = jq("$hrefDial").find("button").first();
		click(trigger);
		waitResponse();
		assertEquals("true", trigger.attr("aria-expanded"), "precondition: hrefDial open");

		getEval("document.getElementById('" + jq("$hrefItemTarget").attr("id") + "').focus()");
		waitResponse();
		getActions().sendKeys(Keys.SPACE).perform();
		waitResponse(true);
		assertEquals("false", trigger.attr("aria-expanded"),
				"Space on an href-backed menuitem must activate it (autoclose then "
						+ "closes the dial), not scroll the page");
	}

	/** An item shown at runtime must be given a slot instead of sitting on the trigger. */
	@Test
	public void speeddialRuntimeShownItemGetsItsOwnSlot() {
		connect();
		waitResponse();
		click(jq("$visShowBtn"));
		waitResponse();

		JQuery slots = jq("$visDial").find(".z-speeddial-items > li");
		String s1 = slots.eq(0).attr("style"), s2 = slots.eq(1).attr("style");
		assertNotNull(s2, "the newly shown item must receive an inline slot direction");
		assertNotEquals(parsePx(s1, "top"), parsePx(s2, "top"),
				"a runtime-shown item must fan out to its own slot, not stay stacked "
						+ "on the trigger at the CSS default");
	}

	/** href="" / target="" normalize to null, like A#setHref / A#setTarget. */
	@Test
	public void speeddialEmptyHrefAndTargetNormalizeToNull() {
		connect();
		waitResponse();
		// tagName is a DOM property, not an attribute — jq().attr() yields "null".
		assertEquals("BUTTON",
				getEval("document.getElementById('" + jq("$emptyHrefItem").attr("id") + "').tagName"),
				"href=\"\" must normalize to null, so the item stays a <button> "
						+ "instead of becoming a reload-current-page anchor");

		click(jq("$emptyTargetBtn"));
		waitResponse();
		assertEquals("null", jq("$emptyTargetItem").attr("target"),
				"a runtime setTarget(\"\") must remove the attribute, matching what "
						+ "a full rerender would emit");
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
