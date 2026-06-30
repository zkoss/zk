/* F104_ZK_6106Test.java

        Purpose:
                
        Description:
                
        History:
                Fri May 15 15:41:09 CST 2026, Created by peakerlee

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
 * Browser-level coverage for Inputmask (PE, ZK-6106).
 * Loads F104-ZK-6106.zul (BaseTestCase.connect() resolves the page from
 * the test class name).
 *
 * Sections marked N/A (with one-line reason in code):
 *  - A4 setter overloads:        N/A — no overloaded setters on Inputmask.
 *  - A6 toString/equals/hashCode: N/A — not overridden.
 *  - A7 Serializable round-trip: out-of-scope — relies on XulElement default.
 *  - B7 responsive viewport:     out-of-scope — viewport-resize testing not in WebDriver suite.
 *  - C8 WCAG contrast:           covered by ./gradlew testWCAGOnly.
 *  - C9 dark theme:              out-of-scope — no dark theme declared.
 *  - G2/G3/G5/G6 lifecycle:      out-of-scope — generic framework concerns.
 *  - I.IME composition:          out-of-scope — synthesising compositionstart/update/end
 *                                across browsers needs CDP-only APIs; manual checklist
 *                                covers Pinyin / Bopomofo / Japanese / Korean.
 *  - I2 very long string:        out-of-scope — generic framework concern.
 *  - I4 locale:                  out-of-scope — i18n MZkex deferred per the JIRA review doc.
 *  - I5 rapid setter chain:      covered by JUnit A1 setter round-trip.
 *  - I6 high latency:            out-of-scope — environment-dependent.
 */
public class F104_ZK_6106Test extends WebDriverTestCase {

	// ────────────────────────────────────────────────────────────────────
	// Basic render
	// ────────────────────────────────────────────────────────────────────

	/** B1: default renders with the z-inputmask class. */
	@Test
	public void inputmaskDefaultRender() {
		connect();
		waitResponse();

		JQuery box = jq("$phoneMask");
		assertTrue(box.exists());
		assertTrue(box.hasClass("z-inputmask"),
				"input should carry z-inputmask sclass");
	}

	/** I1 — null setMask: a literal-only mask still parses without error. */
	@Test
	public void inputmaskLiteralOnlyMaskRenders() {
		connect();
		waitResponse();

		JQuery literal = jq("$literalMask");
		assertTrue(literal.exists(),
				"mask='\\\\99' (literal '9' then digit slot) should render without error");
	}

	// ────────────────────────────────────────────────────────────────────
	// Focus-reveal mask placeholder (imask.js / inputmask.js default behaviour)
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Focusing an empty input reveals the mask pattern so the user can see
	 * the required format. Matches imask.js / inputmask.js / PrimeReact
	 * default — only Cleave.js doesn't do this, and ZK Inputmask used to
	 * be the same outlier.
	 */
	@Test
	public void inputmaskFocusShowsEmptyMaskPattern() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");
		assertEquals("", box.val(), "input starts truly empty before focus");

		click(box);
		waitResponse();
		assertEquals("____", box.val(),
				"focus on empty input should reveal the mask pattern ('____' for mask '9999')");
	}

	/**
	 * Blurring an empty-just-focused input should hide the mask pattern
	 * and revert to truly empty (so the HTML `placeholder` attribute can
	 * show again). No onChange must fire — the server-side value did
	 * not change.
	 */
	@Test
	public void inputmaskBlurEmptyHidesMaskPatternSilently() {
		connect();
		waitResponse();

		JQuery box = jq("$autoClearMask");
		// Click reveals mask pattern; immediate Tab blurs without typing.
		click(box);
		waitResponse();
		assertEquals("____", box.val(),
				"precondition: focus shows mask pattern");
		String logBeforeBlur = jq("$clearLog").text();

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", box.val(),
				"blur on un-typed input should hide the mask pattern (revert to '')");
		assertEquals(logBeforeBlur, jq("$clearLog").text(),
				"no onChange must fire on blur of un-typed input; clearLog must be unchanged");
	}

	/**
	 * Focusing an input that already has typed content (or a server-set
	 * value) must NOT overwrite it with the empty mask pattern.
	 */
	@Test
	public void inputmaskFocusOnFilledInputPreservesValue() {
		connect();
		waitResponse();

		JQuery box = jq("$valueMask");
		// Server-side setValue("1234") gives the input real content first.
		click(jq("$valueSetBtn"));
		waitResponse();
		assertEquals("1234", box.val(), "precondition: value set to 1234");

		// Now focus the input — it must keep "1234", not become "____".
		getEval("document.getElementById('" + box.attr("id") + "').focus()");
		waitResponse();
		assertEquals("1234", box.val(),
				"focus on a filled input must NOT overwrite content with the mask pattern");
	}

	// ────────────────────────────────────────────────────────────────────
	// Visual parity — Inputmask must look like Textbox / Intbox
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Regression guard for the missing-CSS bug the user spotted in review:
	 * when zkex/inputmask.less didn't redeclare the textbox base styles,
	 * the input rendered as a bare browser-default {@code <input>} (shorter,
	 * thinner border, no hover/focus colour). Every other Inputmask test
	 * passed because they only checked the {@code z-inputmask} class /
	 * HTML attributes — none compared computed styles.
	 *
	 * Strategy: place a {@code <textbox>} and an {@code <intbox>} on the
	 * same page as a reference, then assert key computed-style properties
	 * (height, padding, border-width, font-size, background) on Inputmask
	 * match Textbox bit-for-bit. If anyone breaks the .less, this fails.
	 */
	@Test
	public void inputmaskVisualParityWithTextbox() {
		connect();
		waitResponse();

		String[] props = {"height", "padding-top", "padding-right",
				"padding-bottom", "padding-left", "border-top-width",
				"border-right-width", "border-bottom-width", "border-left-width",
				"font-size", "line-height", "background-color"};

		for (String prop : props) {
			String refValue = jq("$referenceTextbox").css(prop);
			String maskValue = jq("$referenceInputmask").css(prop);
			assertEquals(refValue, maskValue,
					"Inputmask " + prop + " must match Textbox (CSS regression check); ref=" + refValue + " mask=" + maskValue);
		}
	}

	/** Inputmask must also match Intbox (the user's reference in the screenshot). */
	@Test
	public void inputmaskVisualParityWithIntbox() {
		connect();
		waitResponse();

		// Spot-check the most visible properties — full property list is
		// covered by inputmaskVisualParityWithTextbox; here we just confirm
		// the user's stated reference (Intbox) also matches.
		String[] props = {"height", "border-top-width", "font-size"};
		for (String prop : props) {
			String refValue = jq("$referenceIntbox").css(prop);
			String maskValue = jq("$referenceInputmask").css(prop);
			assertEquals(refValue, maskValue,
					"Inputmask " + prop + " must match Intbox; ref=" + refValue + " mask=" + maskValue);
		}
	}

	/** Inputmask focus must change border-color (focus state, not just attribute). */
	@Test
	public void inputmaskFocusChangesBorderColor() {
		connect();
		waitResponse();

		JQuery box = jq("$referenceInputmask");
		String idleBorder = box.css("border-top-color");

		// Focus the input programmatically and re-read the computed style.
		getEval("document.getElementById('" + box.attr("id") + "').focus()");
		waitResponse();
		String focusBorder = box.css("border-top-color");

		assertNotEquals(idleBorder, focusBorder,
				"focus must change border-color (CSS :focus rule); idle=" + idleBorder + " focus=" + focusBorder);
	}

	// ────────────────────────────────────────────────────────────────────
	// Typing — per-key regression for the caret-after-remask bug
	// ────────────────────────────────────────────────────────────────────

	/** Batched typing: 'ab12' into mask 'aa-99' produces 'ab-12'. */
	@Test
	public void inputmaskAcceptsLettersAndDigits() {
		connect();
		waitResponse();

		JQuery letters = jq("$lettersMask");
		click(letters);
		waitResponse();
		getActions().sendKeys("ab12").perform();
		waitResponse();
		assertEquals("ab-12", letters.val(),
				"letters then digits should fill mask 'aa-99' as 'ab-12'");
	}

	/**
	 * Per-key typing regression for the caret-after-remask bug. Each
	 * keystroke is sent individually with a waitResponse() so the widget's
	 * _onInput handler runs to completion between keystrokes. Without the
	 * _anchorCaretAtNextSlot fix, the browser resets the caret to the end
	 * of input.value after the re-mask, and subsequent characters fall
	 * outside any slot (never recorded). Batched sendKeys would coalesce
	 * the input events and hide this bug; per-key sendKeys reproduces it.
	 */
	@Test
	public void inputmaskPhonePerKeyTyping() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		// Focus-reveal: empty input on focus shows the mask pattern so the
		// user can see the required format. The first slot is overstrike-
		// selected, so the next keystroke replaces the leading '_'.
		assertEquals("____-___-____", phone.val(),
				"focus on empty input should reveal the mask pattern");

		String[] keys = {"0", "9", "1", "2", "3", "4", "5", "6", "7", "8"};
		String[] expectedAfter = {
				"0___-___-____",
				"09__-___-____",
				"091_-___-____",
				"0912-___-____",
				"0912-3__-____",
				"0912-34_-____",
				"0912-345-____",
				"0912-345-6___",
				"0912-345-67__",
				"0912-345-678_",
		};
		for (int i = 0; i < keys.length; i++) {
			getActions().sendKeys(keys[i]).perform();
			waitResponse();
			assertEquals(expectedAfter[i], phone.val(),
					"after key " + (i + 1) + " ('" + keys[i] + "'), value should be '"
							+ expectedAfter[i] + "', got '" + phone.val() + "'");
		}
	}

	/** Per-key typing into mask 'aa-99' — mixed-token regression for the caret bug. */
	@Test
	public void inputmaskMixedTokenPerKeyTyping() {
		connect();
		waitResponse();

		JQuery letters = jq("$lettersMask");
		click(letters);
		waitResponse();

		getActions().sendKeys("a").perform();
		waitResponse();
		assertEquals("a_-__", letters.val(), "letter slot 1 filled");

		getActions().sendKeys("b").perform();
		waitResponse();
		assertEquals("ab-__", letters.val(),
				"letter slot 2 filled (regression: would lose 'b' if caret stuck at end)");

		getActions().sendKeys("1").perform();
		waitResponse();
		assertEquals("ab-1_", letters.val(), "digit slot 1 filled past literal '-'");

		getActions().sendKeys("2").perform();
		waitResponse();
		assertEquals("ab-12", letters.val(), "digit slot 2 filled, mask saturated");
	}

	/** Typing a digit into a letter slot is rejected. */
	@Test
	public void inputmaskRejectsDigitInLetterSlot() {
		connect();
		waitResponse();

		JQuery letters = jq("$lettersMask");
		click(letters);
		waitResponse();
		getActions().sendKeys("1").perform();
		waitResponse();
		String v = letters.val();
		assertFalse(v != null && v.startsWith("1"),
				"a digit must be rejected at the first LETTER slot");
	}

	/** Plain digits typed into a literal-bearing mask are reformatted. */
	@Test
	public void inputmaskPasteMatchingFormatAcceptedAsIs() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		getActions().sendKeys("1234567890").perform();
		waitResponse();
		assertTrue(phone.val().startsWith("1234-"),
				"typed digits should be reformatted with the mask's literal '-' separators");
	}

	// ────────────────────────────────────────────────────────────────────
	// instant + unmask + autoClear — onChange round-trip
	// ────────────────────────────────────────────────────────────────────

	/** instant=true fires onChange on every slot fill; log accumulates. */
	@Test
	public void inputmaskInstantPerKeyEmitsOneOnChangePerSlot() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		JQuery log = jq("$instantLog");
		click(any);
		waitResponse();

		// anyMask is unmask=true: each per-key onChange must round-trip the
		// slot-only value ("x", then "xy", ...). Placeholder chars must never
		// leak into the committed value (regression: the old extraction sent
		// "x_____" because `*` slots accepted the placeholderChar as content).
		getActions().sendKeys("x").perform();
		waitResponse();
		String afterX = log.text();
		assertTrue(afterX.contains(":x"),
				"instant=true should round-trip 'x' on first slot fill; log=" + afterX);

		getActions().sendKeys("y").perform();
		waitResponse();
		String afterY = log.text();
		assertTrue(afterY.contains(":xy"),
				"second slot fill should round-trip 'xy'; log=" + afterY);

		getActions().sendKeys("z").perform();
		waitResponse();
		String afterZ = log.text();
		assertTrue(afterZ.contains(":xyz"),
				"third slot fill should round-trip 'xyz'; log=" + afterZ);
		assertFalse(afterZ.contains("_"),
				"unmasked values must not contain placeholderChar padding; log=" + afterZ);
	}

	/** Per-key typing reaches the server via the instant pipeline. */
	@Test
	public void inputmaskClientToServerRoundTrip() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		click(any);
		waitResponse();
		getActions().sendKeys("a").perform();
		waitResponse();
		getActions().sendKeys("b").perform();
		waitResponse();
		getActions().sendKeys("c").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		String log = jq("$instantLog").text();
		assertTrue(log.contains(":abc"),
				"per-key typing should round-trip 'abc' to the server; got '" + log + "'");
	}

	/** instant=true emits at least one onChange after batched sendKeys. */
	@Test
	public void inputmaskInstantFiresOnChangePerSlot() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		click(any);
		waitResponse();
		getActions().sendKeys("xyz").perform();
		waitResponse();

		String log = jq("$instantLog").text();
		assertNotEquals("", log,
				"instant=true should emit onChange events as token slots fill");
	}

	/** unmask=true: server-side getValue() returns slot-only string (no literal '-'). */
	@Test
	public void inputmaskUnmaskTrueStripsLiterals() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		click(any);
		waitResponse();
		getActions().sendKeys("abcdef").perform();
		waitResponse();

		String log = jq("$instantLog").text();
		assertFalse(log.contains("-"),
				"unmask=true should yield slot-only values (no literals)");
	}

	/** autoClear=true: partial fill + blur clears + fires onChange null. */
	@Test
	public void inputmaskAutoClearOnBlur() {
		connect();
		waitResponse();

		// Phase 1: an uncommitted partial reverts silently — the display is
		// cleared but no onChange fires (the server value never changed).
		JQuery box = jq("$autoClearMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		assertEquals("", box.val(),
				"autoClear=true should clear the display after blur on partial fill");
		assertEquals("", jq("$clearLog").text(),
				"clearing an uncommitted partial must not fire onChange");

		// Phase 2: once a value has been committed, clearing a later partial
		// commits the cleared value through the standard pipeline.
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("change:1234", jq("$clearLog").text(),
				"complete fill should commit on blur");

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", box.val(),
				"autoClear should clear the partial left by Backspace");
		assertEquals("change:", jq("$clearLog").text(),
				"clearing a previously committed value should fire one onChange with the cleared value");
	}

	// ────────────────────────────────────────────────────────────────────
	// ARIA / accessibility
	// ────────────────────────────────────────────────────────────────────

	/** H1: aria-describedby points to a hidden hint describing the mask format. */
	@Test
	public void inputmaskAriaDescribedbyHint() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		String describedBy = phone.attr("aria-describedby");
		assertNotNull(describedBy);
		assertNotEquals("", describedBy,
				"Inputmask should expose an aria-describedby hint");
	}

	/** D1 + H6: announceProgress=true installs an aria-live region. */
	@Test
	public void inputmaskAnnounceProgressLiveRegion() {
		connect();
		waitResponse();

		JQuery box = jq("$announceMask");
		boolean liveOk = "polite".equals(box.attr("aria-live"))
				|| "assertive".equals(box.attr("aria-live"))
				|| jq("[aria-live=polite]").exists();
		assertTrue(liveOk,
				"announceProgress=true should expose an aria-live region");
	}

	// ────────────────────────────────────────────────────────────────────
	// Breadth — placeholderChar / placeholder / readonly / disabled / value
	// ────────────────────────────────────────────────────────────────────

	/** placeholderChar="X" sends the custom char to the client. */
	@Test
	public void inputmaskSlotCharCustomReachesClient() {
		connect();
		waitResponse();

		JQuery box = jq("$slotCharMask");
		String slot = getEval("zk.Widget.$('" + box.attr("id") + "').$n() && "
				+ "zk.Widget.$('" + box.attr("id") + "')._placeholderChar");
		assertNotNull(slot, "placeholderChar should be set on the client widget");
		assertNotEquals("_", slot,
				"placeholderChar=\"X\" should override the default '_' on the client");
	}

	/** placeholder attribute renders on the input. */
	@Test
	public void inputmaskPlaceholderRenders() {
		connect();
		waitResponse();
		assertEquals("phone", jq("$phoneMask").attr("placeholder"),
				"placeholder=\"phone\" should appear on the rendered input");
	}

	/** readonly=true sets the HTML readonly attribute. */
	@Test
	public void inputmaskReadonlyAttribute() {
		connect();
		waitResponse();

		JQuery box = jq("$readonlyMask");
		String readonly = box.attr("readonly");
		assertTrue("readonly".equals(readonly) || "true".equals(readonly) || readonly != null,
				"readonly=true should expose the readonly attribute on the input");
	}

	/** disabled=true sets the HTML disabled attribute. */
	@Test
	public void inputmaskDisabledAttribute() {
		connect();
		waitResponse();

		JQuery box = jq("$disabledMask");
		String disabled = box.attr("disabled");
		assertTrue("disabled".equals(disabled) || "true".equals(disabled) || disabled != null,
				"disabled=true should expose the disabled attribute on the input");
	}

	/** Server-side setValue("1234") reaches the client input via smartUpdate. */
	@Test
	public void inputmaskValueSetterReachesClient() {
		connect();
		waitResponse();

		JQuery box = jq("$valueMask");
		assertEquals("", box.val(), "input starts empty");

		click(jq("$valueSetBtn"));
		waitResponse();
		assertEquals("1234", box.val(),
				"server-side setValue(\"1234\") should appear on the client input");
	}

	// ────────────────────────────────────────────────────────────────────
	// Depth — backspace / any-token / constraint
	// ────────────────────────────────────────────────────────────────────

	/** Backspace clears the last filled slot. */
	@Test
	public void inputmaskBackspaceClearsLastSlot() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("1234", box.val());

		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		String after = box.val();
		assertFalse(after.startsWith("1234"),
				"backspace should remove the last filled slot; value still '" + after + "'");
	}

	/**
	 * Regression: typing "123" then Backspace once should yield "12__"
	 * (the typed '3' gone). Previously the selection-overstrike anchor on
	 * the next placeholderChar tricked Backspace into "clearing the empty slot
	 * ahead of the caret" (a no-op), so '3' survived and the visible value
	 * stayed "123_". The fix collapses the 1-char-placeholderChar selection back
	 * to a single caret before the existing previous-slot delete logic runs.
	 */
	@Test
	public void inputmaskBackspaceDeletesTypedNotSlotChar() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");
		click(box);
		waitResponse();
		getActions().sendKeys("123").perform();
		waitResponse();
		assertEquals("123_", box.val(),
				"precondition: three digits typed, fourth slot is the placeholder");

		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("12__", box.val(),
				"single Backspace should delete the typed '3', leaving '12__' "
						+ "(not the empty placeholder ahead of the caret)");
	}

	/**
	 * `*` (any) token accepts alphanumeric characters (0-9, a-z, A-Z) and
	 * rejects everything else — symbols, punctuation, whitespace.
	 */
	@Test
	public void inputmaskAnyTokenAcceptsAlphanumericRejectsSpecial() {
		connect();
		waitResponse();

		JQuery box = jq("$anyCharsMask");  // mask="***"
		click(box);
		waitResponse();
		getActions().sendKeys("a1B").perform();
		waitResponse();
		assertEquals("a1B", box.val(),
				"* (any) token should accept alphanumeric characters");

		// Clear the field, then a special char must be rejected: it never
		// lands in a slot, so the display stays the focus-reveal pattern.
		getEval("(function(){var n=document.getElementById('" + box.attr("id")
				+ "');n.setSelectionRange(0,n.value.length);})()");
		getActions().sendKeys(Keys.DELETE).perform();
		waitResponse();
		getActions().sendKeys("$").perform();
		waitResponse();
		assertEquals("___", box.val(),
				"* (any) token must reject a non-alphanumeric char such as '$'");
	}

	/** constraint="no empty": empty-on-blur posts onError to the server. */
	@Test
	public void inputmaskConstraintNoEmptyFiresOnError() {
		connect();
		waitResponse();

		JQuery box = jq("$constraintMask");
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		String log = jq("$constraintLog").text();
		assertTrue(log.startsWith("error:"),
				"empty value with constraint='no empty' should fire onError; log=" + log);
	}

	// ────────────────────────────────────────────────────────────────────
	// Selection-range delete & caret arithmetic across literals
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Selecting a range that crosses a literal then pressing Backspace
	 * should clear every slot inside the selection (literal positions
	 * skipped) and collapse the caret to the selection start.
	 */
	@Test
	public void inputmaskSelectionRangeAcrossLiteralBackspace() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		getActions().sendKeys("0912345").perform();
		waitResponse();
		assertEquals("0912-345-____", phone.val(),
				"precondition: 7 digits typed across two literal '-'");

		// Manually select positions [2, 7) — covers digits '12-34'.
		getEval("(function(){"
				+ "var n=document.getElementById('" + phone.attr("id") + "');"
				+ "n.setSelectionRange(2, 7);"
				+ "})()");
		waitResponse();

		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("095_-___-____", phone.val(),
				"Backspace on selection spanning a literal should clear the "
						+ "covered slots (positions 2,3 + 5,6) only, leaving '0' + '5'; got '" + phone.val() + "'");
	}

	/**
	 * Backspace pressed when the caret sits immediately after a literal
	 * should delete the slot BEFORE the literal (the caret effectively
	 * jumps back over the literal). Without literal-skip, the user would
	 * have to press Backspace twice.
	 */
	@Test
	public void inputmaskBackspaceJumpsOverLiteral() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		getActions().sendKeys("0912").perform();
		waitResponse();
		assertEquals("0912-___-____", phone.val(),
				"precondition: 4 digits typed; caret should be after the '-' literal");

		// Place caret right after the '-' (position 5).
		getEval("(function(){"
				+ "var n=document.getElementById('" + phone.attr("id") + "');"
				+ "n.setSelectionRange(5, 5);"
				+ "})()");
		waitResponse();

		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("091_-___-____", phone.val(),
				"Backspace immediately after '-' should delete the digit BEFORE "
						+ "the literal (caret jumps over)");
	}

	// ────────────────────────────────────────────────────────────────────
	// Arrow / Home / End — skip literals
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Arrow Right at a slot just before a literal should land on the next
	 * SLOT, not on the literal itself — one keypress instead of two.
	 */
	@Test
	public void inputmaskArrowRightSkipsLiteral() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		// Place caret at position 4 (the last digit slot before '-').
		getEval("(function(){"
				+ "var n=document.getElementById('" + phone.attr("id") + "');"
				+ "n.setSelectionRange(4, 4);"
				+ "})()");
		waitResponse();

		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		int pos = Integer.parseInt(getEval(
				"document.getElementById('" + phone.attr("id") + "').selectionStart"));
		assertEquals(5, pos,
				"ArrowRight at position 4 (before '-' at 4) should skip the literal and land at 5");
	}

	/** ArrowLeft symmetric: caret just after literal should jump back over it. */
	@Test
	public void inputmaskArrowLeftSkipsLiteral() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		getEval("(function(){"
				+ "var n=document.getElementById('" + phone.attr("id") + "');"
				+ "n.setSelectionRange(5, 5);"
				+ "})()");
		waitResponse();

		getActions().sendKeys(Keys.ARROW_LEFT).perform();
		waitResponse();
		int pos = Integer.parseInt(getEval(
				"document.getElementById('" + phone.attr("id") + "').selectionStart"));
		assertEquals(3, pos,
				"ArrowLeft at position 5 (after '-' at 4) should skip the literal and land at 3");
	}

	/** Home should jump to the first slot (skipping any leading literals). */
	@Test
	public void inputmaskHomeJumpsToFirstSlot() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		// Caret somewhere in the middle.
		getEval("(function(){"
				+ "var n=document.getElementById('" + phone.attr("id") + "');"
				+ "n.setSelectionRange(8, 8);"
				+ "})()");
		waitResponse();

		getActions().sendKeys(Keys.HOME).perform();
		waitResponse();
		int pos = Integer.parseInt(getEval(
				"document.getElementById('" + phone.attr("id") + "').selectionStart"));
		assertEquals(0, pos, "Home should jump to position 0 (first slot)");
	}

	/**
	 * Shift+Home / Shift+End must extend a text selection (browser-native),
	 * not collapse the caret like bare Home/End do. The keydown handler only
	 * intercepts Home/End when Shift is NOT held, so the browser's
	 * select-to-start / select-to-end behaviour stays available.
	 */
	@Test
	public void inputmaskShiftHomeEndExtendsSelection() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();

		// Shift+Home from a mid-field caret should select back to the start.
		getEval("(function(){"
				+ "var n=document.getElementById('" + phone.attr("id") + "');"
				+ "n.setSelectionRange(8, 8);"
				+ "})()");
		waitResponse();
		getActions().keyDown(Keys.SHIFT).sendKeys(Keys.HOME).keyUp(Keys.SHIFT).perform();
		waitResponse();
		int homeStart = Integer.parseInt(getEval(
				"document.getElementById('" + phone.attr("id") + "').selectionStart"));
		int homeEnd = Integer.parseInt(getEval(
				"document.getElementById('" + phone.attr("id") + "').selectionEnd"));
		assertTrue(homeEnd - homeStart > 0,
				"Shift+Home should extend a selection, not collapse the caret");

		// Shift+End from a near-start caret should select forward to the end.
		getEval("(function(){"
				+ "var n=document.getElementById('" + phone.attr("id") + "');"
				+ "n.setSelectionRange(2, 2);"
				+ "})()");
		waitResponse();
		getActions().keyDown(Keys.SHIFT).sendKeys(Keys.END).keyUp(Keys.SHIFT).perform();
		waitResponse();
		int endStart = Integer.parseInt(getEval(
				"document.getElementById('" + phone.attr("id") + "').selectionStart"));
		int endEnd = Integer.parseInt(getEval(
				"document.getElementById('" + phone.attr("id") + "').selectionEnd"));
		assertTrue(endEnd - endStart > 0,
				"Shift+End should extend a selection, not collapse the caret");
	}

	// Click-on-literal "snap" test removed: position 4 (between slot 3 and
	// the '-' literal) is a VALID caret position — the next typed char lands
	// in slot 4 correctly. _snapToSlot's no-op behaviour at literal boundaries
	// is intentional, not a bug.

	// ────────────────────────────────────────────────────────────────────
	// Ctrl+A / select-all then Delete
	// ────────────────────────────────────────────────────────────────────

	/** Select-all + Delete should clear every typed slot in one keystroke. */
	@Test
	public void inputmaskSelectAllDeleteClearsEverything() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("1234", box.val(), "precondition: 4 digits typed");

		// Select all then press Delete.
		getEval("(function(){"
				+ "var n=document.getElementById('" + box.attr("id") + "');"
				+ "n.setSelectionRange(0, n.value.length);"
				+ "})()");
		waitResponse();
		getActions().sendKeys(Keys.DELETE).perform();
		waitResponse();

		assertEquals("____", box.val(),
				"select-all + Delete should clear every slot");
	}

	// ────────────────────────────────────────────────────────────────────
	// Edge masks — empty / literal-only / long
	// ────────────────────────────────────────────────────────────────────

	/** Empty mask should not constrain input — the widget is a plain textbox. */
	@Test
	public void inputmaskEmptyMaskAcceptsAnything() {
		connect();
		waitResponse();

		JQuery box = jq("$emptyMask");
		assertTrue(box.exists(), "empty mask should still render");
		click(box);
		waitResponse();
		// Server-side: setMask(null) is called when mask is empty (see
		// Inputmask.setMask). Client sees _mask undefined, _onInput is a no-op.
		// Plain typing should pass through to the input.value untouched.
		getActions().sendKeys("any text here").perform();
		waitResponse();
		assertEquals("any text here", box.val(),
				"empty mask should not transform user input");
	}

	/** Literal-only mask "---" has zero slots — typing should be rejected. */
	@Test
	public void inputmaskLiteralOnlyMaskAcceptsNoTyping() {
		connect();
		waitResponse();

		JQuery box = jq("$literalOnlyMask");
		assertTrue(box.exists());
		click(box);
		waitResponse();
		String afterFocus = box.val();
		// Focus reveals the mask pattern, which is just the literals.
		assertEquals("---", afterFocus,
				"focus on literal-only mask should reveal the literal pattern '---'");

		getActions().sendKeys("abc").perform();
		waitResponse();
		// All input should be rejected because there are no slot tokens.
		assertEquals("---", box.val(),
				"literal-only mask should reject every keystroke (no slots to fill)");
	}

	/** Long mask (30 digit slots) should render and accept typing without lag. */
	@Test
	public void inputmaskLongMaskAcceptsManyDigits() {
		connect();
		waitResponse();

		JQuery box = jq("$longMask");
		assertTrue(box.exists());
		click(box);
		waitResponse();
		getActions().sendKeys("123456789012345").perform();  // 15 digits
		waitResponse();
		assertTrue(box.val().startsWith("123-456-789-012-345"),
				"long mask should reformat 15 digits with the literal '-' "
						+ "separators across 5 groups; got '" + box.val() + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// Multi-instance isolation
	// ────────────────────────────────────────────────────────────────────

	// ────────────────────────────────────────────────────────────────────
	// Server-side setValue edge cases — partial / invalid / over-length / null
	// ────────────────────────────────────────────────────────────────────

	/**
	 * setValue with a partially-filled string ("12" for mask "9999") should
	 * leave the input showing the partial value padded with placeholderChars.
	 */
	@Test
	public void inputmaskSetValuePartialFills() {
		connect();
		waitResponse();

		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setPartialBtn"));
		click(jq("$setPartialBtn"));
		waitResponse();
		assertEquals("12__", box.val(),
				"setValue('12') for mask '9999' should pad to '12__' (placeholderChars)");
	}

	/**
	 * setValue with an entirely invalid string ('abcd' for digit mask
	 * '9999') — none of the chars match digit slots, so the displayed
	 * value should end up empty (or placeholderChars only).
	 */
	@Test
	public void inputmaskSetValueInvalidEmptiesDisplay() {
		connect();
		waitResponse();

		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setInvalidBtn"));
		click(jq("$setInvalidBtn"));
		waitResponse();
		String val = box.val();
		// Either truly empty OR all placeholderChars — both signal "no valid input".
		assertTrue(val.equals("") || val.equals("____"),
				"setValue('abcd') for digit mask should render no digits; got '" + val + "'");
	}

	/**
	 * setValue with a string LONGER than the mask should truncate to fit
	 * the available slots (not crash, not error).
	 */
	@Test
	public void inputmaskSetValueOverLengthTruncates() {
		connect();
		waitResponse();

		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setOverBtn"));
		click(jq("$setOverBtn"));
		waitResponse();
		assertEquals("1234", box.val(),
				"setValue('12345') for mask '9999' should truncate to '1234'");
	}

	/** setValue(null) should clear the input. */
	@Test
	public void inputmaskSetValueNullClears() {
		connect();
		waitResponse();

		// Seed a value first so the clear is observable.
		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setOverBtn"));
		click(jq("$setOverBtn"));
		waitResponse();
		assertEquals("1234", box.val(), "precondition: seeded");

		scrollIntoView(jq("$setNullBtn"));
		click(jq("$setNullBtn"));
		waitResponse();
		assertEquals("", box.val(), "setValue(null) should clear the input");
	}

	private void scrollIntoView(JQuery jq) {
		getEval("document.getElementById('" + jq.attr("id") + "').scrollIntoView({block: 'center'})");
	}

	// ────────────────────────────────────────────────────────────────────
	// Real paste event (vs typed string)
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Dispatching a synthetic ClipboardEvent('paste') with text data
	 * should route through the widget's _onPaste handler and apply the
	 * mask. Selenium can't natively trigger paste with custom text, so
	 * we invoke the handler directly with a mock clipboardData.
	 */
	@Test
	public void inputmaskPasteEventReformatsToMask() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		// Simulate paste of raw digits into mask "9999-9999".
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var dt = {getData: function(){return '12345678';}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "return w.$n().value;"
			+ "})()");
		waitResponse();
		assertEquals("1234-5678", box.val(),
				"paste of '12345678' into mask '9999-9999' should be reformatted with '-' separator");
	}

	/**
	 * Paste of pre-formatted text matching the mask (already has '-')
	 * should be accepted as-is.
	 */
	@Test
	public void inputmaskPasteMatchingMaskAcceptedAsIs() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var dt = {getData: function(){return '1234-5678';}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		assertEquals("1234-5678", box.val(),
				"paste of '1234-5678' (already formatted) should remain as-is");
	}

	/**
	 * Paste of mixed valid + invalid chars should be stripped to valid
	 * (via _stripToValid) then refit into slots.
	 */
	@Test
	public void inputmaskPasteMixedValidInvalidStripsThenRefits() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		// Paste "12!@34#$56(7-8" — strip non-alphanumeric, get "12345678",
		// refit into "9999-9999" → "1234-5678".
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var dt = {getData: function(){return '12!@34#$56(7-8';}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		assertEquals("1234-5678", box.val(),
				"paste of mixed valid/invalid should be stripped to valid then refit; got '" + box.val() + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// i18n — full-width digits, RTL layout
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Japanese / CJK IME often emits full-width digits ('０'..'９', U+FF10
	 * +). Inputmask should normalise them to half-width ASCII so the
	 * mask's digit slots accept them.
	 */
	@Test
	public void inputmaskFullWidthDigitsNormalised() {
		connect();
		waitResponse();

		JQuery box = jq("$isolationA");  // mask="9999"
		click(box);
		waitResponse();
		// Type the full-width characters directly via JS so we don't depend
		// on an IME being installed on the test machine.
		getEval(
			"(function(){"
			+ "var n=document.getElementById('" + box.attr("id") + "');"
			+ "n.value='１２３４';"  // '１２３４'
			+ "n.dispatchEvent(new InputEvent('input', {bubbles: true}));"
			+ "})()");
		waitResponse();
		assertEquals("1234", box.val(),
				"full-width digits '１２３４' must be normalised to ASCII '1234'");
	}

	/**
	 * Full-width letters normalisation (mask='aaaa'). Same principle as
	 * digits but covers the A-Z / a-z full-width Unicode blocks.
	 */
	@Test
	public void inputmaskFullWidthLettersNormalised() {
		connect();
		waitResponse();

		JQuery box = jq("$isolationB");  // mask="aaaa"
		click(box);
		waitResponse();
		getEval(
			"(function(){"
			+ "var n=document.getElementById('" + box.attr("id") + "');"
			+ "n.value='ＡＢＣＤ';"  // 'ＡＢＣＤ'
			+ "n.dispatchEvent(new InputEvent('input', {bubbles: true}));"
			+ "})()");
		waitResponse();
		assertEquals("ABCD", box.val(),
				"full-width letters 'ＡＢＣＤ' must be normalised to ASCII 'ABCD'");
	}

	/**
	 * RTL layout (`direction: rtl`) should not break the mask widget —
	 * typing still inserts left-to-right semantically; only the visual
	 * direction of the text flows right-to-left.
	 */
	@Test
	public void inputmaskRtlLayoutTypingWorks() {
		connect();
		waitResponse();

		JQuery box = jq("$rtlMask");
		assertEquals("rtl", box.css("direction"),
				"precondition: RTL style applied");

		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		// Semantic value order is the same as LTR — only visual flow differs.
		assertEquals("12__", box.val(),
				"RTL layout must not change typing semantics; got '" + box.val() + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// IME composition (compositionstart / compositionend)
	// ────────────────────────────────────────────────────────────────────

	/**
	 * IME composition pipeline (Pinyin / Bopomofo / Japanese / Korean):
	 * during composition the widget swallows input, then on
	 * compositionend it merges the composed text into the existing slots.
	 * Selenium can't natively dispatch IME events across browsers, so we
	 * invoke the handler directly with a mock composition event.
	 */
	@Test
	public void inputmaskImeCompositionEndMergesIntoSlots() {
		connect();
		waitResponse();

		JQuery box = jq("$isolationA");  // mask="9999"
		click(box);
		waitResponse();
		// Simulate user typing one digit normally first.
		getActions().sendKeys("1").perform();
		waitResponse();
		assertEquals("1___", box.val(), "precondition: '1' typed");

		// Now simulate an IME compositionend with composed text "234".
		// This mirrors what happens when a CJK IME finishes composing a
		// numeric input (some IMEs route digits through composition).
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "w._composing = true;"  // matches what compositionstart would set
			+ "var ev = {data: '234'};"
			+ "w._onCompEnd(ev);"
			+ "})()");
		waitResponse();
		assertEquals("1234", box.val(),
				"compositionend with data '234' should append to existing '1' giving '1234'");
	}

	// ────────────────────────────────────────────────────────────────────
	// Stress — high-frequency typing, large paste
	// ────────────────────────────────────────────────────────────────────

	/**
	 * High-frequency typing stress: 200 keystrokes into a 4-slot mask. Only
	 * the first 4 digits should land; the remaining 196 must be silently
	 * rejected without state corruption, infinite loops, or crashes.
	 */
	@Test
	public void inputmaskHighFrequencyTypingDoesNotCorruptState() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");  // mask="9999"
		click(box);
		waitResponse();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 200; i++) sb.append((char) ('0' + (i % 10)));
		getActions().sendKeys(sb.toString()).perform();
		waitResponse();

		// 4 digits filled, the remaining 196 keystrokes rejected.
		String val = box.val();
		assertEquals(4, val.length(),
				"after 200 keystrokes into a 4-slot mask the display must still "
						+ "be exactly 4 chars (mask saturation); got '" + val + "'");
	}

	/**
	 * Large-paste stress: paste a 10,000-char string via the widget's
	 * _onPaste pipeline. The mask should truncate to its 4 slots without
	 * exhausting memory or hanging the AU loop.
	 */
	@Test
	public void inputmaskLargePasteIsTruncatedSafely() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");
		click(box);
		waitResponse();

		long start = System.currentTimeMillis();
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var big = new Array(10001).join('9');"  // 10000 '9' chars
			+ "var dt = {getData: function(){return big;}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		long elapsed = System.currentTimeMillis() - start;

		assertEquals("9999", box.val(),
				"10000-char paste into a 4-slot mask should truncate to 4 chars");
		assertTrue(elapsed < 5000,
				"large paste should complete in < 5s; took " + elapsed + "ms");
	}

	/**
	 * Two Inputmask widgets on the same page must not share state — typing
	 * into isolationA must not affect isolationB and vice versa.
	 */
	@Test
	public void inputmaskMultipleInstancesDoNotCrossContaminate() {
		connect();
		waitResponse();

		JQuery a = jq("$isolationA");  // mask="9999"
		JQuery b = jq("$isolationB");  // mask="aaaa"

		click(a);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("1234", a.val());

		click(b);
		waitResponse();
		getActions().sendKeys("abcd").perform();
		waitResponse();
		assertEquals("abcd", b.val());

		// Re-read a — its value must be unchanged by typing into b.
		assertEquals("1234", a.val(),
				"isolationA value must be unchanged by typing into isolationB");
	}

	// ────────────────────────────────────────────────────────────────────
	// Round-2 review regressions (commit pipeline, readonly, paste merge,
	// trailing literal, unmask coercion, IME-after-server-push)
	// ────────────────────────────────────────────────────────────────────

	/** A mask ending in a literal must commit the literal on a complete fill. */
	@Test
	public void inputmaskTrailingLiteralCommittedOnCompleteFill() {
		connect();
		waitResponse();

		JQuery box = jq("$trailingLiteralMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("123").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		assertEquals("(123)", box.val(),
				"complete fill of mask '(999)' should display the trailing ')'");
		assertEquals("change:(123)", jq("$trailingLog").text(),
				"the committed value must include the trailing literal, matching the display");
	}

	/**
	 * instant=true: a rejected char must neither commit a spurious value nor
	 * break the caret — the next valid char still lands in the first slot.
	 */
	@Test
	public void inputmaskInstantRejectedCharKeepsCaretUsable() {
		connect();
		waitResponse();

		JQuery box = jq("$instantDigitMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("a").perform();
		waitResponse();
		getActions().sendKeys("1").perform();
		waitResponse();

		assertEquals("1___", box.val(),
				"after a rejected 'a', the next '1' must land in the first slot (caret intact)");
		assertEquals(":1", jq("$instantDigitLog").text(),
				"the rejected char must not commit a spurious empty onChange");
	}

	/** readonly: Backspace and paste must not mutate the value or commit. */
	@Test
	public void inputmaskReadonlyBlocksBackspaceAndPaste() {
		connect();
		waitResponse();

		JQuery box = jq("$readonlyValueMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.END, Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("1234", box.val(),
				"Backspace on a readonly inputmask must not clear a slot");

		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var dt = {getData: function(){return '9999';}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		assertEquals("1234", box.val(),
				"paste into a readonly inputmask must not replace the value");

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$readonlyLog").text(),
				"no onChange must reach the server for a readonly inputmask");
	}

	/** Paste into a partially filled field appends after the filled slots. */
	@Test
	public void inputmaskPasteIntoFilledFieldMerges() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var dt = {getData: function(){return '34';}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		assertEquals("1234-____", box.val(),
				"paste of '34' after typed '12' should merge into '1234-____'");
	}

	/** A paste contributing no acceptable char must not wipe typed content. */
	@Test
	public void inputmaskPasteGarbageKeepsExistingContent() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var dt = {getData: function(){return 'zz!!';}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		assertEquals("12__-____", box.val(),
				"a paste with no mask-acceptable char must leave the typed '12' untouched");
	}

	/**
	 * unmask=true: an application setValue() with an already-formatted
	 * string must render correctly (tolerant slot fit), not literal-on-
	 * literal garbage — and must survive blur unchanged.
	 */
	@Test
	public void inputmaskUnmaskToleratesFormattedSetValue() {
		connect();
		waitResponse();

		JQuery box = jq("$unmaskValueMask");
		scrollIntoView(jq("$unmaskSetMaskedBtn"));
		click(jq("$unmaskSetMaskedBtn"));
		waitResponse();
		assertEquals("0912-345-6789", box.val(),
				"setValue('0912-345-6789') under unmask=true should refit to the masked display");

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("0912-345-6789", box.val(),
				"the refitted value must survive a focus/blur round-trip (no autoClear wipe)");
	}

	/**
	 * IME composition after a server-side setValue must merge into the
	 * pushed value, not resurrect the stale pre-push display.
	 */
	@Test
	public void inputmaskImeCompositionAfterServerPushMerges() {
		connect();
		waitResponse();

		// Server pushes "12" (display '12__') with no user input in between,
		// then an IME composition of full-width '３４' commits.
		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setPartialBtn"));
		click(jq("$setPartialBtn"));
		waitResponse();
		assertEquals("12__", box.val(), "precondition: server-set partial");

		click(box);
		waitResponse();
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "w._onCompStart();"
			+ "w._onCompEnd({data: '３４'});"
			+ "})()");
		waitResponse();
		assertEquals("1234", box.val(),
				"composition after a server push must merge with the pushed '12', not revert it");
	}

	/**
	 * autoClear + constraint='no empty': clearing a previously committed
	 * value on blur must run constraint validation (onError), like a
	 * user-emptied Textbox.
	 */
	@Test
	public void inputmaskAutoClearRunsConstraintValidation() {
		connect();
		waitResponse();

		JQuery box = jq("$constraintMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$constraintLog").text(),
				"precondition: a complete fill commits without error");

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.END, Keys.BACK_SPACE).perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(jq("$constraintLog").text().startsWith("error:"),
				"autoClear of a committed value must surface the 'no empty' constraint; log="
						+ jq("$constraintLog").text());
	}
	/**
	 * Regression (R4): a rejected char typed mid-field must not destroy the
	 * trailing typed chars. The slot walk used to consume a slot token for
	 * the rejected char, pushing the last char off the end of the token walk
	 * ("1234" + 'x' at caret 2 became "123_").
	 */
	@Test
	public void inputmaskRejectedMidFieldCharKeepsTrailingChars() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("1234", box.val(), "precondition: field fully filled");

		getEval("zk.Widget.$('" + box.attr("id") + "').$n().setSelectionRange(2, 2)");
		getActions().sendKeys("x").perform();
		waitResponse();
		assertEquals("1234", box.val(),
				"a rejected mid-field char must be dropped without eating a slot; got '"
						+ box.val() + "'");
	}

	/**
	 * Regression (R4): a runtime setMask used to parse the OLD display with
	 * the NEW tokens (old literals burned slot positions / leaked into '*'
	 * slots). The typed slot chars must refit the new grid instead.
	 */
	@Test
	public void inputmaskSetMaskRemapsExistingValue() {
		connect();
		waitResponse();

		JQuery box = jq("$remaskMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("12-34", box.val(), "precondition: filled under mask 99-99");

		click(jq("$remaskBtn"));
		waitResponse();
		assertEquals("123-4", box.val(),
				"setMask('999-9') must refit the typed digits 1234 into the new grid; got '"
						+ box.val() + "'");
	}

	/**
	 * Regression (R4): maxlength combined with a mask used to half-apply —
	 * suspended on the client but still enforced by the server's
	 * STRING_TOO_LONG gate, so the field could be filled but never
	 * committed. The Java setter now discards maxlength while a mask is
	 * active: typing and committing the full mask must work.
	 */
	@Test
	public void inputmaskMaxlengthDiscardedWhileMaskActive() {
		connect();
		waitResponse();

		JQuery box = jq("$maxlenMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("1234", box.val(),
				"maxlength=2 must not block typing while mask 9999 is active");

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("change:1234", jq("$maxlenLog").text(),
				"the full fill must commit without a STRING_TOO_LONG error");
		assertFalse(jq(".z-errorbox").exists(),
				"no errorbox: the server-side maxlength gate must not fire under a mask");
	}

	/**
	 * Regression (R4): the full-format paste probe compared the RAW pasted
	 * text against the normalized rebuilt display, so a complete formatted
	 * value pasted in full-width form fell into the merge branch instead of
	 * replacing the field.
	 */
	@Test
	public void inputmaskFullWidthFormattedPasteReplaces() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		assertEquals("12__-____", box.val(), "precondition: two slots filled");

		// "1234-5678" in full-width digits.
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "');"
			+ "var dt = {getData: function(){return '\uFF11\uFF12\uFF13\uFF14-\uFF15\uFF16\uFF17\uFF18';}};"
			+ "var ev = {clipboardData: dt, preventDefault: function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		assertEquals("1234-5678", box.val(),
				"a full-width complete formatted paste must REPLACE the field "
						+ "(same as its half-width form); got '" + box.val() + "'");
	}

	/**
	 * Regression (R4): ArrowLeft used to clamp the caret to position 0 even
	 * when the mask starts with a literal — a collapsed caret before the
	 * leading literal turns the next keystroke into an insertion that shifts
	 * the literal into the slot walk. It must stop at the first slot.
	 */
	@Test
	public void inputmaskArrowLeftStopsAtFirstSlotOnLeadingLiteralMask() {
		connect();
		waitResponse();

		JQuery box = jq("$trailingLiteralMask");
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.ARROW_LEFT, Keys.ARROW_LEFT, Keys.ARROW_LEFT).perform();
		waitResponse();
		assertEquals("1",
				getEval("zk.Widget.$('" + box.attr("id") + "').$n().selectionStart"),
				"ArrowLeft must never park the caret before the leading '(' literal");
	}

	/**
	 * Regression (R5-F2): placeholderChar is a render input — a runtime
	 * setPlaceholderChar must repaint the already-painted placeholder glyphs,
	 * not merely store the field (the prior store-only TS setter left the old
	 * glyph visible until the next keystroke/focus). With autoClear=false a
	 * partial fill stays visible after blur, so the padding is observable:
	 * changing '_' to '#' must turn the at-rest display "12__" into "12##".
	 */
	@Test
	public void inputmaskRuntimePlaceholderCharRepaintsDisplay() {
		connect();
		waitResponse();

		JQuery box = jq("$phcRuntimeMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		assertEquals("12__", box.val(),
				"precondition: partial fill shows the default '_' placeholder padding");

		// Clicking the button blurs the field (autoClear=false keeps "12__")
		// then runs setPlaceholderChar("#") on the server -> smartUpdate -> client.
		click(jq("$phcRuntimeBtn"));
		waitResponse();
		assertEquals("12##", box.val(),
				"runtime setPlaceholderChar('#') must repaint the unfilled-slot glyphs; got '"
						+ box.val() + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// G1: disabled — behavior must be blocked (not just HTML attribute)
	// ────────────────────────────────────────────────────────────────────

	/**
	 * A disabled Inputmask must not process synthetic InputEvents — the widget's
	 * _onInput handler must guard on getDisabled() and return early without
	 * firing onChange. This verifies the behavioral guard, not just the attribute.
	 */
	@Test
	public void inputmaskDisabledBlocksTypingViaEventDispatch() {
		connect();
		waitResponse();

		JQuery box = jq("$disabledBehaviorMask");
		String id = box.attr("id");
		getEval(
			"(function(){"
			+ "var n=document.getElementById('" + id + "');"
			+ "n.value='1';"
			+ "n.dispatchEvent(new InputEvent('input',{bubbles:true}));"
			+ "})()");
		waitResponse();
		assertEquals("", jq("$disabledBehaviorLog").text(),
				"_onInput on a disabled Inputmask must not fire onChange (getDisabled() guard)");
	}

	/**
	 * A disabled Inputmask must reject paste calls — _onPaste must check
	 * getDisabled() and return early, leaving the value and log unchanged.
	 */
	@Test
	public void inputmaskDisabledBlocksPaste() {
		connect();
		waitResponse();

		JQuery box = jq("$disabledBehaviorMask");
		String id = box.attr("id");
		getEval(
			"(function(){"
			+ "var w=zk.Widget.$('" + id + "');"
			+ "var dt={getData:function(){return '1234';}};"
			+ "var ev={clipboardData:dt,preventDefault:function(){}};"
			+ "w._onPaste(ev);"
			+ "})()");
		waitResponse();
		assertEquals("", jq("$disabledBehaviorLog").text(),
				"_onPaste on a disabled Inputmask must not fire onChange");
	}

	// ────────────────────────────────────────────────────────────────────
	// G2: announceProgress — aria-live content must update on typing
	// ────────────────────────────────────────────────────────────────────

	/**
	 * After typing into an announceProgress=true Inputmask, the sibling
	 * aria-live region must contain a non-empty announcement — the live
	 * region is useless if it is installed but never updated.
	 */
	@Test
	public void inputmaskAnnounceProgressLiveRegionUpdatesOnTyping() {
		connect();
		waitResponse();

		JQuery box = jq("$announceMask");
		String id = box.attr("id");
		click(box);
		waitResponse();
		getActions().sendKeys("1").perform();
		waitResponse();

		String liveText = getEval(
			"(function(){"
			+ "var inp=document.getElementById('" + id + "');"
			+ "if (!inp || !inp.parentElement) return '';"
			+ "var live=inp.parentElement.querySelector('[aria-live]');"
			+ "return live ? live.textContent.trim() : '';"
			+ "})()");
		assertNotEquals("", liveText,
				"typing into announceProgress=true must update the aria-live region text; "
				+ "got empty string — live region may be installed but never updated");
	}

	// ────────────────────────────────────────────────────────────────────
	// G3: Delete key — forward delete at a caret position
	// ────────────────────────────────────────────────────────────────────

	/**
	 * The Delete key (forward delete) at a specific caret position must
	 * remove the slot content at that position. Backspace is thoroughly
	 * tested; this test ensures the symmetric Delete direction also works.
	 */
	@Test
	public void inputmaskDeleteKeyClearsSlotAtCaret() {
		connect();
		waitResponse();

		JQuery box = jq("$backspaceMask");  // mask="9999"
		String id = box.attr("id");
		click(box);
		waitResponse();
		getActions().sendKeys("123").perform();
		waitResponse();
		assertEquals("123_", box.val(), "precondition: three digits typed");

		getEval("document.getElementById('" + id + "').setSelectionRange(0, 0)");
		waitResponse();
		getActions().sendKeys(Keys.DELETE).perform();
		waitResponse();

		String val = box.val();
		assertFalse(val.startsWith("1"),
				"Delete at position 0 must remove '1' from the first slot; got '" + val + "'");
		assertTrue(val.contains("2"),
				"Delete must not destroy subsequent slots — '2' must still be present; got '" + val + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// G4: runtime property setters
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Runtime setInstant(true): before the call, typing a single digit must
	 * NOT fire onChange; after the call, the very next slot fill fires
	 * onChange immediately (without a Tab/blur).
	 */
	@Test
	public void inputmaskRuntimeSetInstantEnablesPerKeyOnChange() {
		connect();
		waitResponse();

		JQuery box = jq("$runtimeInstantMask");
		scrollIntoView(box);

		// Phase 1: instant=false — partial digit typed must not fire onChange,
		// only blur after a COMPLETE fill triggers onChange.
		click(box);
		waitResponse();
		getActions().sendKeys("1").perform();
		waitResponse();
		assertEquals("", jq("$runtimeInstantLog").text(),
				"instant=false: typing one digit must NOT fire onChange");

		// Complete the mask fill and then Tab to commit.
		getActions().sendKeys("234").perform();
		waitResponse();
		assertEquals("", jq("$runtimeInstantLog").text(),
				"instant=false: completing the fill must NOT fire onChange mid-type");

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		String logAfterCommit = jq("$runtimeInstantLog").text();
		assertNotEquals("", logAfterCommit,
				"instant=false: Tab (blur) on a complete fill must fire onChange");

		// Phase 2: toggle instant=true.
		scrollIntoView(jq("$runtimeInstantBtn"));
		click(jq("$runtimeInstantBtn"));
		waitResponse();

		// Clear and retype.
		click(box);
		waitResponse();
		getEval("(function(){ var n=document.getElementById('" + box.attr("id") + "'); "
			+ "n.setSelectionRange(0,n.value.length); })()");
		waitResponse();
		getActions().sendKeys(Keys.DELETE).perform();
		waitResponse();

		// One digit — with instant=true, onChange must fire without Tab.
		getActions().sendKeys("5").perform();
		waitResponse();
		assertNotEquals(logAfterCommit, jq("$runtimeInstantLog").text(),
				"after setInstant(true), typing one digit must fire onChange immediately (no Tab)");
	}

	/**
	 * Runtime setAutoClear(true): before the call, partial fill survives blur;
	 * after the call, partial fill is cleared on the next blur.
	 */
	@Test
	public void inputmaskRuntimeSetAutoClearEnablesClear() {
		connect();
		waitResponse();

		JQuery box = jq("$runtimeAutoClearMask");
		scrollIntoView(box);

		// Phase 1: autoClear=false — partial fill survives blur.
		// Clicking the button blurs the mask field (before the onClick fires),
		// so the blur uses autoClear=false; the onClick then sets autoClear=true.
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		scrollIntoView(jq("$runtimeAutoClearBtn"));
		click(jq("$runtimeAutoClearBtn"));
		waitResponse();
		assertEquals("12__", box.val(),
				"autoClear=false: partial '12__' must survive the blur caused by clicking the button");

		// Phase 2: autoClear is now true — next blur must clear the partial.
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", box.val(),
				"after setAutoClear(true), blur on the partial fill must clear the display to ''");
	}

	/**
	 * Runtime setUnmask(true): before the call, committed value includes the
	 * mask's literal characters; after the call, the committed value strips them.
	 */
	@Test
	public void inputmaskRuntimeSetUnmaskStripsLiterals() {
		connect();
		waitResponse();

		JQuery box = jq("$runtimeUnmaskMask");  // mask="99-99", unmask=false
		scrollIntoView(box);

		// Phase 1: unmask=false — committed value includes the literal '-'.
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(jq("$runtimeUnmaskLog").text().contains("-"),
				"unmask=false: committed value must include the literal '-'; log="
				+ jq("$runtimeUnmaskLog").text());

		// Phase 2: setUnmask(true) — next commit strips the literal.
		scrollIntoView(jq("$runtimeUnmaskBtn"));
		click(jq("$runtimeUnmaskBtn"));
		waitResponse();

		// Clear and retype.
		click(box);
		waitResponse();
		getEval("(function(){ var n=document.getElementById('" + box.attr("id") + "'); "
			+ "n.setSelectionRange(0,n.value.length); })()");
		waitResponse();
		getActions().sendKeys(Keys.DELETE).perform();
		waitResponse();
		getActions().sendKeys("5678").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		String log = jq("$runtimeUnmaskLog").text();
		assertFalse(log.contains("-"),
				"after setUnmask(true), committed value must not contain the literal '-'; log=" + log);
		assertTrue(log.contains("5678"),
				"after setUnmask(true), committed value must be '5678' (slots only); log=" + log);
	}

	/**
	 * Runtime setMask(null): after clearing the mask, the widget must revert
	 * to an unconstrained textbox — any text is accepted without reformatting.
	 */
	@Test
	public void inputmaskRuntimeSetMaskNullReverts() {
		connect();
		waitResponse();

		JQuery box = jq("$runtimeNullMaskMask");  // mask="9999"
		scrollIntoView(box);

		// Tab-blur to dismiss the focus-reveal pattern and confirm the field is empty.
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		// Remove the mask via runtime setMask(null).
		scrollIntoView(jq("$runtimeNullMaskBtn"));
		click(jq("$runtimeNullMaskBtn"));
		waitResponse();

		// After mask removal, the widget must accept any text without masking.
		click(box);
		waitResponse();
		getActions().sendKeys("hello").perform();
		waitResponse();
		assertEquals("hello", box.val(),
				"after setMask(null), the widget must accept any text without masking; "
				+ "got '" + box.val() + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// G5: constraint='no empty' — partial fill IS committed as non-empty
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Partial fill (2 of 4 digits) with constraint='no empty' and autoClear=false:
	 * Inputmask DOES commit the partial fill ("12") to the server — it is
	 * non-empty — so constraint='no empty' does NOT fire onError.
	 * This documents that autoClear=false keeps the display AND the server
	 * value reflects the partial entry rather than null.
	 */
	@Test
	public void inputmaskConstraintNoEmptyPartialFillPassesConstraint() {
		connect();
		waitResponse();

		JQuery box = jq("$constraintNoclearMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		// autoClear=false keeps the partial '12__' on screen.
		assertEquals("12__", box.val(),
				"autoClear=false: partial display must remain '12__' after blur");
		// Partial fill "12" is committed as a non-empty value → constraint passes, no onError.
		assertFalse(jq("$constraintNoclearLog").text().startsWith("error:"),
				"constraint='no empty' must NOT fire because partial fill '12' is committed "
				+ "as a non-empty value; log=" + jq("$constraintNoclearLog").text());
	}

	// ────────────────────────────────────────────────────────────────────
	// Optional sections — [ ... ] (ZK-6106 spec: 9/a/* tokens + [] optional)
	// optMask = "(999) 999-9999[ x999]" — phone with an optional extension.
	// ────────────────────────────────────────────────────────────────────

	/** Focus reveal shows the required part only; the optional tail is hidden. */
	@Test
	public void inputmaskOptionalSectionFocusShowsRequiredOnly() {
		connect();
		waitResponse();

		JQuery box = jq("$optMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		assertEquals("(___) ___-____", box.val(),
				"focus reveal must show only the required format, omitting the "
						+ "optional '[ x999]' tail until it is engaged");
	}

	/** Filling only the required slots omits the optional section from display and value. */
	@Test
	public void inputmaskOptionalSectionOmittedWhenUnfilled() {
		connect();
		waitResponse();

		JQuery box = jq("$optMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("0212345678").perform();  // 10 required digits
		waitResponse();
		assertEquals("(021) 234-5678", box.val(),
				"the optional extension must stay hidden while only the required "
						+ "slots are filled");

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("change:(021) 234-5678", jq("$optLog").text(),
				"the committed value must omit the unfilled optional section");
	}

	/** Typing into the optional section reveals it per character and commits it. */
	@Test
	public void inputmaskOptionalSectionIncludedWhenFilled() {
		connect();
		waitResponse();

		JQuery box = jq("$optMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("0212345678").perform();
		waitResponse();

		// One extension digit: the ' x' literal appears and only the typed
		// optional digit shows (per-character optional, no trailing padding).
		getActions().sendKeys("9").perform();
		waitResponse();
		assertEquals("(021) 234-5678 x9", box.val(),
				"the first optional digit reveals the ' x' literal and itself only");

		getActions().sendKeys("99").perform();
		waitResponse();
		assertEquals("(021) 234-5678 x999", box.val(),
				"the full optional section shows once all its slots are filled");

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("change:(021) 234-5678 x999", jq("$optLog").text(),
				"the committed value must include the filled optional section");
	}

	/** Required-only completeness: a full required fill survives blur (optional is not required). */
	@Test
	public void inputmaskOptionalSectionNotRequiredForCompleteness() {
		connect();
		waitResponse();

		JQuery box = jq("$optMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("0212345678").perform();  // all required, no optional
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		// autoClear defaults true: a value is complete once all REQUIRED slots
		// are filled, so it must NOT be cleared despite the empty optional tail.
		assertEquals("(021) 234-5678", box.val(),
				"a full required fill must be treated as complete (optional excluded) "
						+ "and survive blur without autoClear wiping it");
	}
}
