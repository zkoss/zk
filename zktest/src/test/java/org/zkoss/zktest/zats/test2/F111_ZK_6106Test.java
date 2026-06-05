/* F111_ZK_6106Test.java

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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F111_ZK_6106Test extends WebDriverTestCase {

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

	/**
	 * B1b: mold="rounded" is inherited from textbox, so the server accepts it —
	 * the client must be able to render it too. Asserts real DOM, not just the
	 * absence of a server error.
	 */
	@Test
	public void inputmaskRoundedMoldRenders() {
		connect();
		waitResponse();

		JQuery box = jq("$roundedInputmask");
		assertTrue(box.exists(), "mold=\"rounded\" must render an input");
		assertTrue(box.hasClass("z-inputmask"),
				"rounded mold input should carry z-inputmask sclass");
	}

	@Test
	public void inputmaskLiteralOnlyMaskRenders() {
		connect();
		waitResponse();

		JQuery literal = jq("$literalMask");
		click(literal);
		waitResponse();
		assertEquals("9_", literal.val());
		getActions().sendKeys("5").perform();
		waitResponse();
		assertEquals("95", literal.val());
	}

	// ────────────────────────────────────────────────────────────────────
	// Focus-reveal mask placeholder (imask.js / inputmask.js default behaviour)
	// ────────────────────────────────────────────────────────────────────

	/** Focusing an empty input reveals the empty mask pattern. */
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

		JQuery box = jq("$strictFillMask");
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

	/** Inputmask's computed styles match a Textbox on the same page. */
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
					"Inputmask " + prop + " must match Textbox (CSS regression check); ref=" + refValue + " format=" + maskValue);
		}
	}

	/** Inputmask's key computed styles also match an Intbox. */
	@Test
	public void inputmaskVisualParityWithIntbox() {
		connect();
		waitResponse();

		String[] props = {"height", "border-top-width", "font-size"};
		for (String prop : props) {
			String refValue = jq("$referenceIntbox").css(prop);
			String maskValue = jq("$referenceInputmask").css(prop);
			assertEquals(refValue, maskValue,
					"Inputmask " + prop + " must match Intbox; ref=" + refValue + " format=" + maskValue);
		}
	}

	/** Inside an Inputgroup, Inputmask matches a grouped Textbox at rest and on focus. */
	@Test
	public void inputmaskVisualParityWithTextboxInInputgroup() {
		connect();
		waitResponse();

		JQuery textbox = jq("$groupedTextbox");
		JQuery mask = jq("$groupedInputmask");
		String[] props = {"border-top-width", "border-left-width", "border-top-color", "padding-left", "flex-grow"};
		for (String prop : props) {
			assertEquals(textbox.css(prop), mask.css(prop),
					"grouped Inputmask " + prop + " must match the grouped Textbox");
		}

		getEval("document.getElementById('" + textbox.attr("id") + "').focus()");
		waitResponse();
		String focusBorder = textbox.css("border-top-width");
		String focusPadding = textbox.css("padding-left");
		getEval("document.getElementById('" + mask.attr("id") + "').focus()");
		waitResponse();
		assertEquals(focusBorder, mask.css("border-top-width"),
				"grouped Inputmask must keep the grouped Textbox's border width on focus");
		assertEquals(focusPadding, mask.css("padding-left"),
				"grouped Inputmask must keep the grouped Textbox's padding on focus");
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
	// Typing — per-key caret placement after each re-mask
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

	/** Per-key typing fills every slot (batched sendKeys would coalesce the input events). */
	@Test
	public void inputmaskPhonePerKeyTyping() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		// Focus-reveal: empty input on focus shows the mask pattern so the
		// user can see the required format, with the caret on the first slot.
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

	/** The caret stays collapsed while typing: a selected '_' would be read out by screen readers. */
	@Test
	public void inputmaskTypingKeepsCaretCollapsed() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		assertEquals("0,0", selection(phone), "focus puts a collapsed caret on the first slot");
		String[] keys = {"0", "9", "1", "2"};
		String[] carets = {"1,1", "2,2", "3,3", "5,5"};
		for (int i = 0; i < keys.length; i++) {
			getActions().sendKeys(keys[i]).perform();
			waitResponse();
			assertEquals(carets[i], selection(phone), "after key '" + keys[i] + "'");
		}
		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("3,3", selection(phone), "Backspace across the literal leaves a collapsed caret");
	}

	/** A click past the typed content puts the caret after it, so a paste there continues the fill. */
	@Test
	public void inputmaskClickPastContentThenPasteContinuesFill() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		click(phone);
		waitResponse();
		getActions().sendKeys("09").perform();
		waitResponse();
		// The x offset, from the box centre, of the gap before display char 10 ("09__-___-_|___").
		int dx = Integer.parseInt(getEval("(function () { var n = jq('#" + phone.attr("id") + "')[0], cs = getComputedStyle(n),"
				+ " c = document.createElement('canvas').getContext('2d'); c.font = cs.font;"
				+ " return Math.round(parseFloat(cs.borderLeftWidth) + parseFloat(cs.paddingLeft)"
				+ " + c.measureText(n.value.slice(0, 10)).width - n.getBoundingClientRect().width / 2); })()"));
		getActions().moveToElement(toElement(phone), dx, 0).click().perform();
		waitResponse();
		assertEquals("2,2", selection(phone), "the caret goes after the typed \"09\"");
		paste(phone, "1234567");
		assertEquals("0912-345-67__", phone.val());
	}

	@Test
	public void inputmaskPastePastTypedContentContinuesFill() {
		connect();
		waitResponse();

		JQuery phone = jq("$phoneMask");
		pastePastTypedContent(phone, "09", "3,3", "123456789");
		assertEquals("0912-345-6789", phone.val());
	}

	@Test
	public void inputmaskPastePastALiteralReadsFromTheFirstEmptySlot() {
		connect();
		waitResponse();

		JQuery digits = jq("$pasteLiteralMask");
		pastePastTypedContent(digits, "1", "3,3", "234");
		assertEquals("12-34", digits.val());

		JQuery letters = jq("$lettersMask");
		pastePastTypedContent(letters, "A", "3,3", "B1");
		assertEquals("AB-1_", letters.val());
	}

	/** Types {@code typed}, steps past it with ArrowRight and pastes {@code text} there. */
	private void pastePastTypedContent(JQuery box, String typed, String caret, String text) {
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys(typed, Keys.ARROW_RIGHT).perform();
		waitResponse();
		assertEquals(caret, selection(box), "precondition: the caret is past the typed content");
		paste(box, text);
	}

	private String selection(JQuery box) {
		return getEval("(function () { var n = jq('#" + box.attr("id") + "')[0]; return n.selectionStart + ',' + n.selectionEnd; })()");
	}

	/** Per-key typing into mask 'aa-99' fills letter and digit slots in order. */
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
				"letter slot 2 filled");

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
		assertEquals("__-__", letters.val(),
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
	// instant + strictFill — onChange round-trip
	// ────────────────────────────────────────────────────────────────────

	/** instant=true commits a complete fill once, after the onChangingDelay pause, without a blur. */
	@Test
	public void inputmaskInstantCommitsCompleteFillOnce() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		JQuery log = jq("$instantLog");
		click(any);
		waitResponse();

		// The placeholderChar never reaches the committed value of a `*` slot.
		getActions().sendKeys("xyzuvw").perform();
		waitFor(() -> !log.text().isEmpty());
		waitResponse();
		assertEquals(":xyzuvw", log.text(), "one commit for the burst, slot-only, no padding");
	}

	/** Per-key typing reaches the server via the instant pipeline. */
	@Test
	public void inputmaskClientToServerRoundTrip() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		click(any);
		waitResponse();
		getActions().sendKeys("abc").perform();
		waitResponse();
		getActions().sendKeys("def").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		String log = jq("$instantLog").text();
		assertEquals(":abcdef", log, "only the complete fill reaches the server");
	}

	/** instant=true + strictFill: the commit after the typing pause refuses a partial fill. */
	@Test
	public void inputmaskInstantPartialFillIsNotCommitted() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		click(any);
		waitResponse();
		getActions().sendKeys("xyz").perform();
		waitFor(() -> any.hasClass("z-inputmask-invalid"));
		waitResponse();

		assertTrue(any.hasClass("z-inputmask-invalid"), "the partial fill must be refused");
		assertEquals("xyz-___", any.val(), "the typed text must stay");
		assertEquals("", jq("$instantLog").text(), "a partial fill must not commit");
	}

	/** Server-side getValue() returns the slot-only string (no literal '-'). */
	@Test
	public void inputmaskValueStripsLiterals() {
		connect();
		waitResponse();

		JQuery any = jq("$anyMask");
		click(any);
		waitResponse();
		getActions().sendKeys("abcdef").perform();
		waitResponse();

		String log = jq("$instantLog").text();
		assertFalse(log.contains("-"),
				"the value should be slot-only (no literals)");
	}

	/** strictFill=true: a partial fill is refused on blur, never erased. */
	@Test
	public void inputmaskStrictFillRefusesPartialOnBlur() {
		connect();
		waitResponse();

		JQuery box = jq("$strictFillMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		assertEquals("12__", box.val(), "the typed text must stay after blur");
		assertTrue(box.hasClass("z-inputmask-invalid"), "the field must be marked invalid");
		assertTrue(jq(".z-errorbox").exists(), "the standard errorbox must be shown");
		assertTrue(jq("$strictErrorLog").text().startsWith("error:Illegal value ____"),
				"onError must carry the illegal-value message and the empty pattern; log="
						+ jq("$strictErrorLog").text());
		assertEquals("", jq("$clearLog").text(), "a refused partial must not commit");

		// Completing the fill commits it and clears the error.
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.END, "34").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("1234", box.val());
		assertFalse(box.hasClass("z-inputmask-invalid"), "a complete fill must clear the invalid state");
		assertFalse(jq(".z-errorbox").exists(), "a complete fill must close the errorbox");
		assertEquals("change:1234", jq("$clearLog").text());
	}

	/** instant + strictFill: the debounced commit of a partial fill is refused like a blur. */
	@Test
	public void inputmaskInstantStrictFillRefusesPartialAtDebouncedCommit() {
		connect();
		waitResponse();

		JQuery box = jq("$instantStrictMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitFor(() -> jq(".z-errorbox").exists() || !jq("$instantStrictLog").text().isEmpty());
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(), "the debounced commit of a partial fill must show the errorbox");
		assertFalse(Arrays.asList(jq("$instantStrictLog").text().split("\\|")).contains("12"),
				"a partial fill must never reach onChange; log=" + jq("$instantStrictLog").text());

		click(jq("$instantStrictValueBtn"));
		waitResponse();
		assertEquals("value:", jq("$instantStrictValueLog").text(), "the server value must stay unchanged");

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.END, "34").perform();
		waitFor(() -> !jq(".z-errorbox").exists() && jq("$instantStrictLog").text().endsWith("|1234"));
		waitResponse();
		assertFalse(jq(".z-errorbox").exists(), "a complete fill must close the errorbox");
		assertTrue(jq("$instantStrictLog").text().endsWith("|1234"), "log=" + jq("$instantStrictLog").text());
	}

	/** Polls well past the 350 ms onChanging debounce; the caller asserts the outcome. */
	private void waitFor(BooleanSupplier condition) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(3)).until(d -> condition.getAsBoolean());
		} catch (TimeoutException ex) {
			// fall through to the caller's assertions
		}
	}

	/** The errorbox describes the field alongside the format hint, never instead of it. */
	@Test
	public void inputmaskErrorboxKeepsFormatHint() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		JQuery box = jq("$constraintMask");
		String hintId = box.attr("id") + "-hint";
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals(hintId + " " + jq(".z-errorbox").attr("id"), box.attr("aria-describedby"),
				"opening the errorbox must add to the hint, not replace it");

		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertFalse(jq(".z-errorbox").exists(), "precondition: the error is cleared");
		assertEquals(hintId, box.attr("aria-describedby"),
				"closing the errorbox must remove only its own id");
	}

	// ────────────────────────────────────────────────────────────────────
	// ARIA / accessibility
	// ────────────────────────────────────────────────────────────────────

	/** H1: aria-describedby points to a hidden hint describing the mask format. */
	@Test
	public void inputmaskAriaDescribedbyHint() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		JQuery phone = jq("$phoneMask");
		String describedBy = phone.attr("aria-describedby");
		assertEquals(phone.attr("id") + "-hint", describedBy,
				"Inputmask should expose an aria-describedby hint");
	}

	@Test
	public void inputmaskAnnounceProgressIsOffByDefault() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		click(jq("$announceOffMask"));
		waitResponse();
		getActions().sendKeys("12").perform();
		sleep(1000);
		assertEquals("", progressAnnouncements());
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
		assertEquals("X", slot,
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
		assertEquals("readonly", box.attr("readonly"),
				"readonly=true should expose the readonly attribute on the input");
	}

	/** disabled=true sets the HTML disabled attribute. */
	@Test
	public void inputmaskDisabledAttribute() {
		connect();
		waitResponse();

		JQuery box = jq("$disabledMask");
		assertEquals("disabled", box.attr("disabled"),
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
		assertEquals("123_", box.val(), "backspace should remove the last filled slot");
	}

	/** Typing "123" then Backspace yields "12__": the typed '3' goes, not the empty slot ahead of the caret. */
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

		JQuery box = jq("$anyCharsMask");  // format="***"
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

	@Test
	public void inputmaskArrowKeysCollapseASelectionToItsEdge() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		String n = "jq('#" + box.attr("id") + "')[0]";
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		eval("(function () { var n = " + n + "; n.setSelectionRange(0, n.value.length); })()");
		getActions().sendKeys(Keys.ARROW_RIGHT, "9").perform();
		waitResponse();
		assertEquals("129_-____", box.val(), "ArrowRight collapses the selection to its end");

		eval(n + ".setSelectionRange(1, 3)");
		getActions().sendKeys(Keys.ARROW_LEFT).perform();
		waitResponse();
		assertEquals("1,1", selection(box), "ArrowLeft collapses the selection to its start");
	}

	@Test
	public void inputmaskArrowLeftOnALiteralSelectionStaysAtTheLiteral() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		eval("jq('#" + box.attr("id") + "')[0].setSelectionRange(4, 5)");
		getActions().sendKeys(Keys.ARROW_LEFT).perform();
		waitResponse();
		assertEquals("4,4", selection(box), "the selected '-' collapses to its start, as natively");
		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("123_-____", box.val(), "Backspace then deletes the char before the literal");
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
		// An empty format is null: plain typing passes through untouched.
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

	/** setRawValue("12") on "9999" shows the partial value padded: "12__". */
	@Test
	public void inputmaskSetRawValuePartialFills() {
		connect();
		waitResponse();

		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setPartialBtn"));
		click(jq("$setPartialBtn"));
		waitResponse();
		assertEquals("12__", box.val(),
				"setRawValue('12') for mask '9999' should pad to '12__' (placeholderChars)");
	}

	/** setValue("abcd") on "9999" is refused, not silently emptied. */
	@Test
	public void inputmaskSetValueInvalidIsRefused() {
		connect();
		waitResponse();

		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setFullBtn"));
		click(jq("$setFullBtn"));
		waitResponse();
		scrollIntoView(jq("$setInvalidBtn"));
		click(jq("$setInvalidBtn"));
		waitResponse();
		assertEquals("1234", box.val(), "setValue('abcd') for mask '9999' must keep the value");
		assertTrue(jq(".z-errorbox").exists(), "setValue('abcd') for mask '9999' must be refused");
	}

	/** setValue("12345") on "9999" is refused, not silently truncated. */
	@Test
	public void inputmaskSetValueOverLengthIsRefused() {
		connect();
		waitResponse();

		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setFullBtn"));
		click(jq("$setFullBtn"));
		waitResponse();
		scrollIntoView(jq("$setOverBtn"));
		click(jq("$setOverBtn"));
		waitResponse();
		assertEquals("1234", box.val(), "setValue('12345') for mask '9999' must keep the value");
		assertTrue(jq(".z-errorbox").exists(), "setValue('12345') for mask '9999' must be refused");
	}

	/** value="12" before strictFill="false" / readonly="true" still loads. */
	@Test
	public void inputmaskValueBeforeStrictFillOrReadonlyLoads() {
		connect();
		waitResponse();

		assertEquals("12__", jq("$orderStrictMask").val());
		assertEquals("12__", jq("$orderReadonlyMask").val());
	}

	/** isValid() is false whenever the next getValue() refuses a partial fill. */
	@Test
	public void inputmaskIsValidAgreesWithDeferredFillCheck() {
		assertEquals("isValid=false getValue=threw", clickServerCheck("$srvIsValidBtn"));
	}

	/** A read while readonly does not disarm the fill check for after readonly is lifted. */
	@Test
	public void inputmaskReadonlyKeepsFillCheckArmed() {
		assertEquals("threw", clickServerCheck("$srvReadonlyBtn"));
	}

	/** setValue then getValue runs the constraint once, as on Textbox. */
	@Test
	public void inputmaskSetValueRunsConstraintOnce() {
		assertEquals("calls=1", clickServerCheck("$srvConstraintBtn"));
	}

	/** A full-width placeholderChar hole is refused on the server, as on the client. */
	@Test
	public void inputmaskFullWidthFillerHoleRefusedOnServer() {
		assertEquals("threw", clickServerCheck("$srvFullWidthBtn"));
	}

	@Test
	public void inputmaskSetFormatAfterMaxlengthThrows() {
		assertEquals("maxlength:threw", clickServerCheck("$maxlenFirstBtn"));
	}

	@Test
	public void inputmaskMultiCharPlaceholderCharThrows() {
		assertEquals("threw:#", clickServerCheck("$phcTooLongBtn"));
	}

	@Test
	public void inputmaskNullOrEmptyPlaceholderCharRestoresDefault() {
		assertEquals("_|_", clickServerCheck("$phcResetBtn"));
	}

	@Test
	public void inputmaskCollidingPlaceholderCharFallsBackOnServer() {
		assertEquals("1_:__", clickServerCheck("$fillerTextBtn"));
	}

	@Test
	public void inputmaskServerSetTextReadsBareSlots() {
		assertEquals("1234567890|1234567890|1234567890123", clickServerCheck("$bareTextBtn"));
		assertEquals("+1 (123) 456-7890", jq("$bareTextLooseMask").val());
	}

	@Test
	public void inputmaskClientSetTextReadsBareSlots() {
		connect();
		waitResponse();

		JQuery box = jq("$digitLiteralMask");
		eval("zk.Widget.$('" + box.attr("id") + "').setText('1234567890')");
		assertEquals("+1 (123) 456-7890", box.val());
	}

	private String clickServerCheck(String button) {
		connect();
		waitResponse();
		scrollIntoView(jq(button));
		click(jq(button));
		waitResponse();
		return jq("$srvLog").text();
	}

	/** Client setValue of bare slots keeps a digit that equals a format literal ("+1"). */
	@Test
	public void inputmaskClientSetValueBareSlotsWithDigitLiteral() {
		connect();
		waitResponse();

		JQuery box = jq("$digitLiteralMask");
		eval("zk.Widget.$('" + box.attr("id") + "').setValue('1234567890')");
		assertEquals("+1 (123) 456-7890", box.val());
	}

	/** Alt+Backspace (Mac) / Ctrl+Backspace after typing deletes the word. */
	@Test
	public void inputmaskModifiedBackspaceDeletesTyped() {
		connect();
		waitResponse();

		JQuery box = jq("$modBackspaceMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("123").perform();
		waitResponse();
		Keys mod = getEval("navigator.platform").startsWith("Mac") ? Keys.ALT : Keys.CONTROL;
		getActions().keyDown(mod).sendKeys(Keys.BACK_SPACE).keyUp(mod).perform();
		waitResponse();
		assertEquals("____", box.val());
	}

	/** Blur reads the display, so a shown letter literal never becomes data ("**[X**]"). */
	@Test
	public void inputmaskBlurKeepsLetterLiteralOutOfValue() {
		connect();
		waitResponse();

		JQuery box = jq("$altLiteralMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("123").perform();
		waitResponse();
		assertEquals("12X3", box.val(), "precondition");
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("123", getEval("zk.Widget.$('" + box.attr("id") + "').getValue()"));
	}

	/** A Backspace the input takes natively (keyCode 229, Android IME) clears the previous slot. */
	@Test
	public void inputmaskNativeBackspaceClearsPreviousSlot() {
		connect();
		waitResponse();

		JQuery box = jq("$androidBackspaceMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		assertEquals("12__", box.val(), "precondition");
		nativeBackspace(box, "1__", 1);
		assertEquals("1___", box.val());
	}

	/** A native Backspace that removed only a literal ("12-|34") clears the slot before it instead. */
	@Test
	public void inputmaskNativeBackspaceAfterLiteralClearsPreviousSlot() {
		connect();
		waitResponse();

		JQuery box = jq("$androidLiteralBackspaceMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("12-34", box.val(), "precondition");
		eval("jq('#" + box.attr("id") + "')[0].setSelectionRange(3, 3)");
		nativeBackspace(box, "1234", 2);
		assertEquals("13-4_", box.val(), "the '2' before the literal must go, the literal stays");
	}

	/** Replays a Backspace the input applies itself: beforeinput, the browser's edit, then input. */
	private void nativeBackspace(JQuery box, String after, int caret) {
		eval("(function () { var n = jq('#" + box.attr("id") + "')[0];"
				+ "n.dispatchEvent(new InputEvent('beforeinput', {inputType: 'deleteContentBackward', bubbles: true, cancelable: true}));"
				+ "n.value = '" + after + "'; n.setSelectionRange(" + caret + ", " + caret + ");"
				+ "n.dispatchEvent(new InputEvent('input', {inputType: 'deleteContentBackward', bubbles: true})); })()");
		waitResponse();
	}

	/** A placeholderChar swap is no edit: focus + blur of the untouched partial value shows no errorbox. */
	@Test
	public void inputmaskPlaceholderCharSwapIsNoEdit() {
		connect();
		waitResponse();

		JQuery box = jq("$phcUntouchedMask");
		scrollIntoView(box);
		eval("zk.Widget.$('" + box.attr("id") + "').setValue('12', true).setPlaceholderChar('#')");
		assertEquals("12##", box.val(), "precondition");
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertFalse(jq(".z-errorbox").exists());
	}

	/** A client setFormat('') keeps the value ("1234"), not the formatted text ("12-34"). */
	@Test
	public void inputmaskClientFormatRemovalShowsValue() {
		connect();
		waitResponse();

		JQuery box = jq("$fmtClearMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("1234", Keys.TAB).perform();
		waitResponse();
		eval("zk.Widget.$('" + box.attr("id") + "').setFormat('')");
		assertEquals("1234", box.val());
	}

	/** A formatted value pushed from the server (stateless withValue) is held as bare slots, as a user commit is. */
	@Test
	public void inputmaskServerPushedFormattedValueIsSlots() {
		connect();
		waitResponse();

		JQuery box = jq("$srvPushMask");
		eval("zk.Widget.$('" + box.attr("id") + "').setValue('1234-5678', true)");
		assertEquals("1234-5678", box.val(), "precondition");
		assertEquals("12345678", getEval("zk.Widget.$('" + box.attr("id") + "').getValue()"));
	}

	/** A full-width digit literal ("９99") stays a literal when the client re-reads the display. */
	@Test
	public void inputmaskFullWidthLiteralStaysLiteral() {
		connect();
		waitResponse();

		assertEquals("９12", jq("$fwLiteralMask").val());
	}

	/** The server reads a full-width literal in setText as the literal, not as slot data. */
	@Test
	public void inputmaskFullWidthLiteralStaysLiteralOnServer() {
		assertEquals("12", clickServerCheck("$fwLiteralBtn"));
	}

	/** errorMessage="@load(...)" goes through ErrorMessageConverter, so getValue() still returns the value. */
	@Test
	public void inputmaskBoundErrorMessageKeepsValueReadable() {
		assertEquals("ok", clickServerCheck("$bindErrBtn"));
	}

	/** setValue(null) should clear the input. */
	@Test
	public void inputmaskSetValueNullClears() {
		connect();
		waitResponse();

		// Seed a value first so the clear is observable.
		JQuery box = jq("$edgeValueMask");
		scrollIntoView(jq("$setFullBtn"));
		click(jq("$setFullBtn"));
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

	/** A ClipboardEvent('paste') carrying text goes through the widget's paste handler and is fitted to the mask. */
	@Test
	public void inputmaskPasteEventReformatsToMask() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		paste(box, "12345678");
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
		paste(box, "1234-5678");
		assertEquals("1234-5678", box.val(),
				"paste of '1234-5678' (already formatted) should remain as-is");
	}

	/**
	 * A mixed paste keeps only the chars each slot accepts, then refits.
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
		paste(box, "12!@34#$56(7-8");
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

		JQuery box = jq("$isolationA");  // format="9999"
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

		JQuery box = jq("$isolationB");  // format="aaaa"
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

		JQuery box = jq("$isolationA");  // format="9999"
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
			+ "var w = zk.Widget.$('" + box.attr("id") + "'), n = w.$n();"
			+ "w._doCompositionStart();"
			+ "n.setRangeText('234', n.selectionStart, n.selectionEnd, 'end');"
			+ "w._doCompositionEnd();"
			+ "})()");
		waitResponse();
		assertEquals("1234", box.val(),
				"compositionend with data '234' should append to existing '1' giving '1234'");
	}

	@Test
	public void inputmaskImeRecompositionDoesNotDuplicate() {
		connect();
		waitResponse();

		JQuery box = jq("$anyCharsMask");  // format="***"
		click(box);
		waitResponse();
		getActions().sendKeys("a").perform();
		waitResponse();
		assertEquals("a__", box.val());

		// Android IME re-composition: region 'a' becomes 'ab'; data repeats the whole word.
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "'), n = w.$n();"
			+ "n.setSelectionRange(1, 1);"
			+ "w._doCompositionStart();"
			+ "n.setRangeText('ab', 0, 1, 'end');"
			+ "w._doCompositionEnd();"
			+ "})()");
		waitResponse();
		assertEquals("ab_", box.val());
	}

	@Test
	public void inputmaskImeEventsLeaveThePreeditUnmasked() {
		connect();
		waitResponse();

		JQuery box = jq("$changingMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		assertEquals("____", box.val(), "precondition");
		String n = "jq('#" + box.attr("id") + "')[0]";
		eval("(function () { var n = " + n + ";"
				+ "n.dispatchEvent(new CompositionEvent('compositionstart', {bubbles: true}));"
				+ "n.dispatchEvent(new InputEvent('beforeinput', {inputType: 'insertCompositionText', data: '１', isComposing: true, bubbles: true, cancelable: true}));"
				+ "n.value = '１____'; n.setSelectionRange(1, 1);"
				+ "n.dispatchEvent(new InputEvent('input', {inputType: 'insertCompositionText', data: '１', isComposing: true, bubbles: true})); })()");
		sleep(1000);
		waitResponse();
		assertEquals("１____", box.val(), "mid-composition the preedit must not be re-masked");
		assertEquals("", jq("$changingLog").text(), "mid-composition no onChanging is sent");

		eval(n + ".dispatchEvent(new CompositionEvent('compositionend', {data: '１', bubbles: true}))");
		assertEquals("1___", box.val());
		sleep(1000);
		waitResponse();
		assertEquals("|1___", jq("$changingLog").text());
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

		JQuery box = jq("$backspaceMask");  // format="9999"
		click(box);
		waitResponse();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 200; i++) sb.append((char) ('0' + (i % 10)));
		getActions().sendKeys(sb.toString()).perform();
		waitResponse();

		// 4 digits filled, the remaining 196 keystrokes rejected.
		assertEquals("0123", box.val(),
				"after 200 keystrokes into a 4-slot mask the first 4 digits must stay (mask saturation)");
	}

	/**
	 * Large-paste stress: paste a 10,000-char string via the widget's
	 * paste pipeline. The mask should truncate to its 4 slots without
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
		paste(box, String.join("", Collections.nCopies(10000, "9")));
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

		JQuery a = jq("$isolationA");  // format="9999"
		JQuery b = jq("$isolationB");  // format="aaaa"

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
	// Commit pipeline, readonly, paste merge, trailing literal,
	// formatted setValue, IME after a server push
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
		assertEquals("change:123|(123)", jq("$trailingLog").text(),
				"the value is the slot characters; the trailing literal belongs to the text");
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
		getActions().sendKeys("234").perform();
		waitFor(() -> !jq("$instantDigitLog").text().isEmpty());
		waitResponse();
		assertEquals(":1234", jq("$instantDigitLog").text(),
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

		paste(box, "9999");
		assertEquals("1234", box.val(),
				"paste into a readonly inputmask must not replace the value");

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$readonlyLog").text(),
				"no onChange must reach the server for a readonly inputmask");
	}

	/** A paste at the caret after the filled slots continues the fill. */
	@Test
	public void inputmaskPasteIntoFilledFieldMerges() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		paste(box, "34");
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
		paste(box, "zz!!");
		assertEquals("12__-____", box.val(),
				"a paste with no mask-acceptable char must leave the typed '12' untouched");
	}

	/** setValue() with an already-formatted string refits and survives blur. */
	@Test
	public void inputmaskSetValueToleratesFormattedString() {
		connect();
		waitResponse();

		JQuery box = jq("$unmaskValueMask");
		scrollIntoView(jq("$unmaskSetMaskedBtn"));
		click(jq("$unmaskSetMaskedBtn"));
		waitResponse();
		assertEquals("0912-345-6789", box.val(),
				"setValue('0912-345-6789') should refit to the masked display");

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("0912-345-6789", box.val(),
				"the refitted value must survive a focus/blur round-trip");
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
			+ "var w = zk.Widget.$('" + box.attr("id") + "'), n = w.$n();"
			+ "w._doCompositionStart();"
			+ "n.setRangeText('３４', n.selectionStart, n.selectionEnd, 'end');"
			+ "w._doCompositionEnd();"
			+ "})()");
		waitResponse();
		assertEquals("1234", box.val(),
				"composition after a server push must merge with the pushed '12', not revert it");
	}

	/**
	 * constraint='no empty': clearing a previously committed value on blur
	 * must run constraint validation (onError), like a user-emptied Textbox.
	 */
	@Test
	public void inputmaskClearingCommittedValueRunsConstraintValidation() {
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
		getActions().sendKeys(Keys.END, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE).perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(jq("$constraintLog").text().startsWith("error:"),
				"clearing a committed value must surface the 'no empty' constraint; log="
						+ jq("$constraintLog").text());
	}
	/** A rejected char typed mid-field keeps the trailing typed chars. */
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

	/** A runtime setFormat refits the typed slot chars into the new grid. */
	@Test
	public void inputmaskSetFormatRemapsExistingValue() {
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
				"setFormat('999-9') must refit the typed digits 1234 into the new grid; got '"
						+ box.val() + "'");
	}

	/**
	 * maxlength and a mask are mutually exclusive: the setter must refuse
	 * loudly (Bandbox/Combobox precedent) instead of silently discarding the
	 * caller's maxlength.
	 */
	@Test
	public void inputmaskMaxlengthWithMaskThrows() {
		connect();
		waitResponse();

		click(jq("$maxlenBtn"));
		waitResponse();
		assertEquals("threw", jq("$maxlenLog").text(),
				"setMaxlength() under a mask must throw UnsupportedOperationException");

		JQuery box = jq("$maxlenMask");
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		waitResponse();
		assertEquals("1234", box.val(),
				"the refused maxlength must not have been applied");
	}

	/** A full-width formatted paste over the whole field replaces it, as its half-width form does. */
	@Test
	public void inputmaskFullWidthFormattedPasteOverSelectionReplaces() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		assertEquals("12__-____", box.val(), "precondition: two slots filled");

		// "1234-5678" in full-width digits, pasted over the whole field.
		pasteAt(box, 0, 9, "\uFF11\uFF12\uFF13\uFF14-\uFF15\uFF16\uFF17\uFF18");
		assertEquals("1234-5678", box.val());
	}

	/** A paste with the caret before the filled slots is inserted there, as typing is. */
	@Test
	public void inputmaskPasteAtCaretInsertsThere() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		pasteAt(box, 0, 0, "9");
		assertEquals("912_-____", box.val());
	}

	/** A paste over a selection replaces only the selected slots. */
	@Test
	public void inputmaskPasteOverSelectionReplacesIt() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12345678").perform();
		waitResponse();
		pasteAt(box, 2, 4, "99");
		assertEquals("1299-5678", box.val());
	}

	/** A paste into an optional tail ("99[99]" holding "12") continues the fill instead of replacing it. */
	@Test
	public void inputmaskPasteIntoOptionalTailContinuesFill() {
		connect();
		waitResponse();

		JQuery box = jq("$pasteOptMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		pasteAt(box, 2, 2, "34");
		assertEquals("1234", box.val());
	}

	/** A formatted paste keeps its leading literals as formatting, even with a digit literal ("+1 ("). */
	@Test
	public void inputmaskFormattedPasteAlignsLiteralsBeforeCaret() {
		connect();
		waitResponse();

		JQuery box = jq("$digitLiteralMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		pasteAt(box, 4, 4, "+1 (555) 123-4567");
		assertEquals("+1 (555) 123-4567", box.val());
	}

	/** A bare run of slot characters fills from the caret, so a leading '1' is data, not the "+1" literal. */
	@Test
	public void inputmaskBareSlotPasteFillsFromCaret() {
		connect();
		waitResponse();

		JQuery box = jq("$digitLiteralMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		pasteAt(box, 4, 4, "1234567890");
		assertEquals("+1 (123) 456-7890", box.val());
	}

	private void pasteAt(JQuery box, int start, int end, String text) {
		eval("jq('#" + box.attr("id") + "')[0].setSelectionRange(" + start + ", " + end + ")");
		assertFalse(paste(box, text), "the paste handler cancels the native insert");
	}

	/**
	 * Dispatches the paste event a user paste raises; being untrusted, it inserts nothing natively.
	 * Returns whether the event was left uncancelled.
	 */
	private boolean paste(JQuery box, String text) {
		boolean uncancelled = Boolean.parseBoolean(getEval("(function(){ var n = jq('#" + box.attr("id") + "')[0], dt = new DataTransfer();"
				+ "dt.setData('text/plain', '" + text + "');"
				+ "return n.dispatchEvent(new ClipboardEvent('paste', {clipboardData: dt, bubbles: true, cancelable: true})); })()"));
		waitResponse();
		return uncancelled;
	}

	/** ArrowLeft stops at the first slot when the mask starts with a literal. */
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

	/** A runtime setPlaceholderChar repaints the padding: "12__" becomes "12##". */
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

		// Clicking the button blurs the field (strictFill=false keeps "12__")
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

	/** A disabled Inputmask ignores a synthetic input event. */
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
		assertEquals("1", box.val(), "a disabled Inputmask must not re-mask a synthetic input");
		assertEquals("", jq("$disabledBehaviorLog").text(),
				"doInput_ on a disabled Inputmask must not fire onChange");
	}

	/** A disabled Inputmask rejects a paste, leaving the value and log unchanged. */
	@Test
	public void inputmaskDisabledBlocksPaste() {
		connect();
		waitResponse();

		JQuery box = jq("$disabledBehaviorMask");
		paste(box, "1234");
		assertEquals("", box.val(), "a disabled Inputmask must not accept a paste");
		assertEquals("", jq("$disabledBehaviorLog").text(),
				"paste on a disabled Inputmask must not fire onChange");
	}

	// ────────────────────────────────────────────────────────────────────
	// Progress live region (za11y) — updates on typing
	// ────────────────────────────────────────────────────────────────────

	/** A burst of keystrokes is announced once, with the count the pause settles on. */
	@Test
	public void inputmaskAnnounceProgressAnnouncesTheSettledCount() {
		connect();
		waitResponse();

		if (Boolean.parseBoolean(getEval("typeof msgza11y === 'undefined'")))
			return; // spoken only by za11y
		click(jq("$announceMask"));
		waitResponse();
		getActions().sendKeys("12").perform();
		sleep(1000);
		assertEquals("2 of 4 filled", progressAnnouncements());

		getActions().sendKeys("x").perform();
		sleep(1000);
		assertEquals("2 of 4 filled", progressAnnouncements(), "a rejected keystroke is not re-announced");

		getActions().sendKeys("3").perform();
		sleep(1000);
		assertEquals("2 of 4 filled|3 of 4 filled", progressAnnouncements());
	}

	@Test
	public void inputmaskAnnounceProgressSkipsARefusedKeystrokeAfterABlur() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		JQuery box = jq("$announceMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		sleep(1000);
		assertEquals("2 of 4 filled", progressAnnouncements(), "precondition");

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		click(box);
		waitResponse();
		getActions().sendKeys("x").perform();
		sleep(1000);
		assertEquals("2 of 4 filled", progressAnnouncements(), "the blur keeps the count already spoken");
	}

	@Test
	public void inputmaskAnnounceProgressSkipsARefusedFirstKeystroke() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		click(jq("$announceMask"));
		waitResponse();
		getActions().sendKeys("x").perform();
		sleep(1000);
		assertEquals("", progressAnnouncements(), "the count the field loaded with is not spoken");
	}

	@Test
	public void inputmaskAnnounceProgressSkipsARefusedKeystrokeAfterASetValue() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		JQuery box = jq("$announceMask");
		eval("zk.Widget.$('" + box.attr("id") + "').setValue('12', true)");
		click(box);
		waitResponse();
		getActions().sendKeys("x").perform();
		sleep(1000);
		assertEquals("", progressAnnouncements(), "the count setValue left is not spoken");
	}

	@Test
	public void inputmaskRuntimeSetAnnounceProgressReachesClient() {
		connect();
		waitResponse();

		JQuery box = jq("$announceRuntimeMask");
		scrollIntoView(jq("$announceRuntimeBtn"));
		click(jq("$announceRuntimeBtn"));
		waitResponse();
		assertEquals("true", getEval("String(zk.Widget.$('" + box.attr("id") + "')._announceProgress)"));
		if (!isA11yLoaded())
			return;

		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		sleep(1000);
		assertEquals("2 of 4 filled", progressAnnouncements());
	}

	/** The progress messages in the za11y polite announcer, joined by '|'. */
	private String progressAnnouncements() {
		return getEval("Array.prototype.slice.call(document.querySelectorAll("
				+ "'#za11y-announcer [aria-live=polite] > div')).map(function (d) { return d.textContent; })"
				+ ".filter(function (t) { return t.indexOf(' filled') >= 0; }).join('|')");
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

		JQuery box = jq("$backspaceMask");  // format="9999"
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
	 * Runtime setInstant(true): before the call, typing must NOT fire
	 * onChange until blur; after the call, a complete fill fires onChange
	 * after the typing pause (without a Tab/blur).
	 */
	@Test
	public void inputmaskRuntimeSetInstantCommitsWithoutBlur() {
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
		assertEquals(":change:1234", jq("$runtimeInstantLog").text(),
				"instant=false: Tab (blur) on a complete fill must fire onChange once");

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

		// A complete fill — with instant=true, onChange must fire without Tab.
		getActions().sendKeys("5678").perform();
		waitFor(() -> jq("$runtimeInstantLog").text().endsWith(":change:5678"));
		waitResponse();
		assertTrue(jq("$runtimeInstantLog").text().endsWith(":change:5678"),
				"after setInstant(true), a complete fill must fire onChange without Tab; log="
						+ jq("$runtimeInstantLog").text());
	}

	/**
	 * Runtime setStrictFill(true): before the call a partial fill commits;
	 * after it, the next blur revalidates the unchanged partial and refuses it.
	 */
	@Test
	public void inputmaskRuntimeSetStrictFillRevalidates() {
		connect();
		waitResponse();

		JQuery box = jq("$runtimeStrictFillMask");
		scrollIntoView(box);

		// Clicking the button blurs the field before its onClick runs.
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		scrollIntoView(jq("$runtimeStrictFillBtn"));
		click(jq("$runtimeStrictFillBtn"));
		waitResponse();
		assertEquals("12__", box.val());
		assertEquals("change:12", jq("$runtimeStrictFillLog").text(),
				"strictFill=false: the partial must commit");
		assertFalse(jq(".z-errorbox").exists());

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(box.hasClass("z-inputmask-invalid"),
				"after setStrictFill(true) the next blur must refuse the unchanged partial");
		assertTrue(jq(".z-errorbox").exists());
	}


	/**
	 * Runtime setFormat(null): after clearing the mask, the widget must revert
	 * to an unconstrained textbox — any text is accepted without reformatting.
	 */
	@Test
	public void inputmaskRuntimeSetFormatNullReverts() {
		connect();
		waitResponse();

		JQuery box = jq("$runtimeNullMaskMask");  // format="9999"
		scrollIntoView(box);

		// Tab-blur to dismiss the focus-reveal pattern and confirm the field is empty.
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		// Remove the mask via runtime setFormat(null).
		scrollIntoView(jq("$runtimeNullMaskBtn"));
		click(jq("$runtimeNullMaskBtn"));
		waitResponse();

		// After mask removal, the widget must accept any text without masking.
		click(box);
		waitResponse();
		getActions().sendKeys("hello").perform();
		waitResponse();
		assertEquals("hello", box.val(),
				"after setFormat(null), the widget must accept any text without masking; "
				+ "got '" + box.val() + "'");
	}

	// ────────────────────────────────────────────────────────────────────
	// G5: constraint='no empty' — partial fill IS committed as non-empty
	// ────────────────────────────────────────────────────────────────────

	/**
	 * Partial fill (2 of 4 digits) with constraint='no empty' and strictFill=false:
	 * Inputmask DOES commit the partial fill ("12") to the server — it is
	 * non-empty — so constraint='no empty' does NOT fire onError.
	 * This documents that strictFill=false keeps the display AND the server
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

		// strictFill=false keeps the partial '12__' on screen.
		assertEquals("12__", box.val(),
				"strictFill=false: partial display must remain '12__' after blur");
		assertFalse(box.hasClass("z-inputmask-invalid"), "strictFill=false must accept the partial");
		assertEquals("change:12", jq("$constraintNoclearChangeLog").text(),
				"the partial fill '12' must be committed as a non-empty value");
		assertEquals("", jq("$constraintNoclearLog").text(),
				"constraint='no empty' must NOT fire for the committed partial fill");
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
		assertEquals("change:0212345678|(021) 234-5678", jq("$optLog").text(),
				"value = the 10 required slot chars; text = the formatted display, "
						+ "with the unfilled optional section omitted");
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
		assertEquals("change:0212345678999|(021) 234-5678 x999", jq("$optLog").text(),
				"value = required + filled optional slot chars; text = the formatted display");
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
		// strictFill defaults true: a value is complete once all REQUIRED slots
		// are filled, so it must NOT be refused despite the empty optional tail.
		assertEquals("(021) 234-5678", box.val(),
				"a full required fill must be treated as complete (optional excluded) "
						+ "and survive blur");
		assertFalse(box.hasClass("z-inputmask-invalid"), "an empty optional tail is not incomplete");
	}

	@Test
	public void inputmaskHintDescribesFormat() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		assertEquals("Format: 4 digit(s), '-', 3 digit(s), '-', 4 digit(s)", hintText("$phoneMask"));
		assertEquals("Format: '$', 3 digit(s), '.', 2 digit(s)", hintText("$currencyMask"));
		assertEquals("Format: '(', 3 digit(s), ')', ' ', 3 digit(s), '-', 4 digit(s)", hintText("$optMask"));
		assertEquals("Format: 3 letter(s) a to z or digit(s), '-', 3 letter(s) a to z or digit(s)", hintText("$anyMask"));
		assertEquals("Format: 2 letter(s) a to z, '-', 2 digit(s)", hintText("$lettersMask"));
	}

	@Test
	public void inputmaskSetFormatRewritesHint() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		assertEquals("Format: 2 digit(s), '-', 2 digit(s)", hintText("$remaskMask"), "precondition");
		click(jq("$remaskBtn"));
		waitResponse();
		assertEquals("Format: 3 digit(s), '-', 1 digit(s)", hintText("$remaskMask"));
	}

	@Test
	public void inputmaskDetachRemovesHint() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		String hint = "document.getElementById('" + jq("$detachMask").attr("id") + "-hint')";
		assertEquals("true", getEval("String(!!" + hint + ")"), "precondition");
		scrollIntoView(jq("$detachBtn"));
		click(jq("$detachBtn"));
		waitResponse();
		assertFalse(jq("$detachMask").exists(), "precondition: detached");
		assertEquals("false", getEval("String(!!" + hint + ")"));
	}

	private String hintText(String selector) {
		return getEval("document.getElementById('" + jq(selector).attr("id") + "-hint').textContent");
	}

	@Test
	public void inputmaskTabThroughFiresNoOnChanging() {
		connect();
		waitResponse();

		JQuery box = jq("$changingMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$changingLog").text());

		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		sleep(1000);
		waitResponse();
		String log = jq("$changingLog").text();
		assertTrue(log.endsWith("|12__"), "onChanging carries the masked text; log=" + log);
	}

	/** onChanging waits for onChangingDelay like any input: a quick burst sends one. */
	@Test
	public void inputmaskOnChangingIsDebounced() {
		connect();
		waitResponse();

		JQuery box = jq("$changingLongMask");
		scrollIntoView(box);
		countClientOnChanging(box);
		click(box);
		waitResponse();
		getActions().sendKeys("0912345678").perform();
		sleep(1000);
		waitResponse();
		assertEquals("1", getEval("window.onChangingFired"), "one onChanging for the whole burst");
		assertEquals("|(091) 234-5678", jq("$changingLongLog").text());

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("1", getEval("window.onChangingFired"),
				"the blur flush must not repeat an already-sent value");
	}

	/** A blur before onChangingDelay elapses flushes the pending onChanging once. */
	@Test
	public void inputmaskOnChangingFlushedOnBlur() {
		connect();
		waitResponse();

		JQuery box = jq("$changingMask");
		scrollIntoView(box);
		countClientOnChanging(box);
		click(box);
		waitResponse();
		getActions().sendKeys("1234", Keys.TAB).perform();
		waitResponse();
		assertEquals("1", getEval("window.onChangingFired"));
		assertEquals("|1234", jq("$changingLog").text());
	}

	// Counts client fires: the AU queue merges a burst, so the server log cannot.
	private void countClientOnChanging(JQuery box) {
		eval("(function () { window.onChangingFired = 0; zk.Widget.$('" + box.attr("id")
				+ "').listen({onChanging: function () { window.onChangingFired++; }}); })()");
	}

	/** Backspace edits under preventDefault, so no input event re-arms the timer the keydown stopped. */
	@Test
	public void inputmaskOnChangingAfterBackspace() {
		connect();
		waitResponse();

		JQuery box = jq("$changingMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("1234").perform();
		sleep(1000);
		waitResponse();
		getActions().sendKeys(Keys.BACK_SPACE).perform();
		sleep(1000);
		waitResponse();
		assertEquals("|1234|123_", jq("$changingLog").text());
	}

	/** Required slots decide completeness, so an optional section must be trailing. */
	@Test
	public void inputmaskOptionalSectionMustBeTrailing() {
		connect();
		waitResponse();

		scrollIntoView(jq("$optionalMidBtn"));
		click(jq("$optionalMidBtn"));
		waitResponse();
		assertEquals("threw", jq("$optionalMidLog").text());
	}

	/** Inputmask shows the same focus indication as Textbox. */
	@Test
	public void inputmaskFocusRingMatchesTextbox() {
		connect();
		waitResponse();

		String outline = "(function(){var s=getComputedStyle(document.activeElement);"
				+ "return s.outlineStyle+' '+s.outlineWidth+' '+s.outlineOffset;})()";
		click(jq("$referenceTextbox"));
		waitResponse();
		String textbox = getEval(outline);
		click(jq("$referenceInputmask"));
		waitResponse();
		assertEquals(textbox, getEval(outline));
	}

	@Test
	public void inputmaskReadonlyPartialValueSurvivesBlur() {
		connect();
		waitResponse();

		JQuery box = jq("$readonlyPartialMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("12__", box.val());
		assertEquals("", jq("$readonlyPartialLog").text());
	}

	@Test
	public void inputmaskArrowRightPastFilledSlotsKeepsTyping() {
		connect();
		waitResponse();

		JQuery box = jq("$phoneMask");
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		for (int i = 0; i < 15; i++)
			getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		getActions().sendKeys("3").perform();
		waitResponse();
		assertEquals("123_-___-____", box.val());
	}

	@Test
	public void inputmaskBackspacePastTypedContentDeletesTheLastTypedChar() {
		connect();
		waitResponse();

		JQuery box = jq("$phoneMask");
		click(box);
		waitResponse();
		getActions().sendKeys("09", Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT).perform();
		waitResponse();
		assertEquals("6,6", selection(box), "precondition: the caret is past the typed content");
		getActions().sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("0___-___-____", box.val());
		assertEquals("1,1", selection(box));
	}

	@Test
	public void inputmaskDeleteBeforeLiteralClearsNextSlot() {
		connect();
		waitResponse();

		JQuery box = jq("$phoneMask");
		String id = box.attr("id");
		click(box);
		waitResponse();
		getActions().sendKeys("09123456789").perform();
		waitResponse();
		assertEquals("0912-345-6789", box.val());

		getEval("document.getElementById('" + id + "').setSelectionRange(4, 4)");
		waitResponse();
		getActions().sendKeys(Keys.DELETE).perform();
		waitResponse();
		assertEquals("0912-456-789_", box.val());
		assertEquals(4, Integer.parseInt(getEval(
				"document.getElementById('" + id + "').selectionStart")));
	}

	@Test
	public void inputmaskShiftDeleteCutsSelection() {
		connect();
		waitResponse();

		JQuery box = jq("$phoneMask");
		String id = box.attr("id");
		click(box);
		waitResponse();
		getActions().sendKeys("09123456789").perform();
		waitResponse();
		getEval("(function(){var n=document.getElementById('" + id + "');"
				+ "n.addEventListener('cut',function(){window.inputmaskCut=true;});"
				+ "n.setSelectionRange(5,8);})()");
		getActions().keyDown(Keys.SHIFT).sendKeys(Keys.DELETE).keyUp(Keys.SHIFT).perform();
		waitResponse();
		assertTrue(Boolean.parseBoolean(getEval("!!window.inputmaskCut")));
		assertEquals("0912-678-9___", box.val());
	}

	@Test
	public void inputmaskReadonlyEnterKeepsFieldBlank() {
		connect();
		waitResponse();

		JQuery box = jq("$readonlyOkMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		assertEquals("ok", jq("$okLog").text());
		assertEquals("", box.val());
	}

	@Test
	public void inputmaskEnterOnEmptyFieldFiresNoClientOnChange() {
		connect();
		waitResponse();

		JQuery box = jq("$emptyOkMask");
		scrollIntoView(box);
		getEval("(function(){zk.Widget.$('" + box.attr("id") + "').listen({onChange: function () {"
				+ "window.inputmaskChanges = (window.inputmaskChanges || 0) + 1;}});})()");
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		assertEquals("ok", jq("$okLog").text());
		assertEquals("0", getEval("String(window.inputmaskChanges || 0)"));
		assertEquals("____", box.val(), "the focused field keeps the bare pattern");
	}

	@Test
	public void inputmaskCollidingPlaceholderCharFallsBack() {
		connect();
		waitResponse();

		JQuery box = jq("$fillerCollisionMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		assertEquals("__:__", box.val());
		getActions().sendKeys("1030").perform();
		waitResponse();
		assertEquals("10:30", box.val());
	}

	@Test
	public void inputmaskFullWidthPlaceholderCharFallsBack() {
		connect();
		waitResponse();

		JQuery box = jq("$fullWidthFillerMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		assertEquals("____", box.val());
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$fullWidthFillerLog").text(), "an untouched field must not commit the filler as digits");

		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		assertEquals("12__", box.val());
	}

	@Test
	public void inputmaskDeleteKeepsMixedSlotsInPlace() {
		connect();
		waitResponse();

		JQuery box = jq("$mixedDeleteMask");
		scrollIntoView(box);
		assertEquals("AB-12", box.val());
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.HOME, Keys.ARROW_RIGHT, Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("B_-12", box.val(), "the digits must stay in their slots");
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$mixedDeleteLog").text(), "an empty slot before a filled one must not commit");
		assertTrue(jq(".z-errorbox").exists());

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.HOME, Keys.ARROW_RIGHT, "C", Keys.TAB).perform();
		waitResponse();
		assertEquals("change:BC12", jq("$mixedDeleteLog").text());
	}

	@Test
	public void inputmaskImeFillsAnEmptySlot() {
		connect();
		waitResponse();

		JQuery box = jq("$mixedDeleteMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.HOME, Keys.ARROW_RIGHT, Keys.BACK_SPACE).perform();
		waitResponse();
		assertEquals("B_-12", box.val());
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		getEval(
			"(function(){"
			+ "var w = zk.Widget.$('" + box.attr("id") + "'), n = w.$n();"
			+ "w._doCompositionStart();"
			+ "n.setRangeText('Ｃ', n.selectionStart, n.selectionEnd, 'end');"
			+ "w._doCompositionEnd();"
			+ "})()");
		waitResponse();
		assertEquals("BC-12", box.val(), "a composed char must fill the empty slot it was typed into");
	}

	@Test
	public void inputmaskInsertKeepsEscapedLiteralOutOfData() {
		connect();
		waitResponse();

		JQuery box = jq("$escapedLiteralMask");
		scrollIntoView(box);
		assertEquals("12934__", box.val());
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.HOME, "5", Keys.TAB).perform();
		waitResponse();
		assertEquals("519234_", box.val());
		assertEquals("change:51234", jq("$escapedLiteralLog").text());
	}

	@Test
	public void inputmaskValueBeforeFormatIsFitted() {
		connect();
		waitResponse();

		JQuery box = jq("$valueFirstMask");
		scrollIntoView(box);
		assertEquals("0912-345-6789", box.val());
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("0912-345-6789", box.val());
		assertEquals("", jq("$valueFirstLog").text());
	}

	@Test
	public void inputmaskForgedOnChangeIsRejected() {
		connect();
		waitResponse();

		JQuery box = jq("$forgedMask");
		scrollIntoView(box);
		eval("zAu.send(new zk.Event(zk.Widget.$('" + box.attr("id") + "'), 'onChange', {value: '12ab'}))");
		waitResponse();
		assertEquals("", jq("$forgedLog").text());
		assertTrue(jq(".z-errorbox").exists(), "the server must refuse a value that is not a slot string");
	}

	@Test
	public void inputmaskForgedPartialOnChangeIsRejected() {
		connect();
		waitResponse();

		JQuery box = jq("$forgedMask");
		scrollIntoView(box);
		eval("zAu.send(new zk.Event(zk.Widget.$('" + box.attr("id") + "'), 'onChange', {value: '12'}))");
		waitResponse();
		assertEquals("", jq("$forgedLog").text(), "strictFill must refuse a partial fill sent by the client");
		assertTrue(jq(".z-errorbox").exists());
	}

	@Test
	public void inputmaskSetFormatDropsValueTheNewGridRejects() {
		connect();
		waitResponse();

		JQuery box = jq("$refitMask");
		scrollIntoView(box);
		assertEquals("abc", box.val(), "precondition");
		click(jq("$refitBtn"));
		waitResponse();
		assertEquals("", box.val());
		assertEquals("value:", jq("$refitValueLog").text(), "the server must drop the letters as the client does");

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$refitLog").text(), "a blur after setFormat must not commit anything");
	}

	@Test
	public void inputmaskSetFormatNullKeepsTheValue() {
		connect();
		waitResponse();

		JQuery box = jq("$unmaskNullMask");
		scrollIntoView(box);
		assertEquals("12-34", box.val(), "precondition");
		click(jq("$unmaskNullBtn"));
		waitResponse();
		assertEquals("1234", box.val(), "the client shows the value the server resends with the format");
		assertEquals("value:1234", jq("$unmaskNullValueLog").text());

		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq("$unmaskNullLog").text(), "a blur after setFormat(null) must not commit anything");
	}

	@Test
	public void inputmaskCustomConstraintCannotCommitPartialFill() {
		connect();
		waitResponse();

		JQuery box = jq("$customCstMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("12").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		assertNotEquals("change:12", jq("$customCstLog").text(),
				"a CustomConstraint must not let the server commit a partial fill");
		assertTrue(jq("$customCstErrLog").text().startsWith("error:") || box.hasClass("z-inputmask-invalid"),
				"the refused partial fill must surface an error");
	}

	@Test
	public void inputmaskStrictFillSetValuePartialThrows() {
		connect();
		waitResponse();

		scrollIntoView(jq("$strictSetValueBtn"));
		click(jq("$strictSetValueBtn"));
		waitResponse();
		assertEquals("threw", jq("$strictSetValueLog").text());
	}

	@Test
	public void inputmaskRequiredLiteralAfterOptionalSectionThrows() {
		connect();
		waitResponse();

		scrollIntoView(jq("$optionalLiteralBtn"));
		click(jq("$optionalLiteralBtn"));
		waitResponse();
		assertEquals("threw", jq("$optionalLiteralLog").text());
	}

	@Test
	public void inputmaskTrailingOptionalSectionIsAccepted() {
		connect();
		waitResponse();

		scrollIntoView(jq("$optionalTrailingBtn"));
		click(jq("$optionalTrailingBtn"));
		waitResponse();
		assertEquals("no-throw", jq("$optionalLiteralLog").text());
	}

	@Test
	public void inputmaskErrorTextHidesFormatSyntax() {
		connect();
		waitResponse();

		JQuery box = jq("$rawSyntaxMask");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("1").perform();
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		assertTrue(jq(".z-errorbox").exists(), "precondition: the partial fill is refused");
		String msg = jq(".z-errorbox-content").text();
		assertFalse(msg.contains("\\") || msg.contains("[") || msg.contains("]"),
				"the error text must not expose the raw format syntax: " + msg);
	}

	@Test
	public void errorboxKeepsAuthoredDescribedby() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		JQuery box = jq("$authoredDescTextbox");
		scrollIntoView(box);
		assertEquals("authored-id", box.attr("aria-describedby"));
		click(box);
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(), "precondition: the errorbox is open");
		assertEquals("authored-id", box.attr("aria-describedby"));

		click(box);
		waitResponse();
		getActions().sendKeys("x", Keys.TAB).perform();
		waitResponse();
		assertFalse(jq(".z-errorbox").exists(), "precondition: the error is cleared");
		assertEquals("authored-id", box.attr("aria-describedby"));
	}

	@Test
	public void errorboxReopenAddsNoDuplicateDescribedbyToken() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		JQuery box = jq("$plainCstTextbox");
		scrollIntoView(box);
		click(box);
		waitResponse();
		getActions().sendKeys("a", Keys.TAB).perform();
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(), "precondition: the errorbox is open");
		String errboxId = jq(".z-errorbox").attr("id");
		assertEquals(errboxId, describedBy(box));

		// Focusing the still-invalid field opens the same errorbox again.
		click(box);
		waitResponse();
		assertEquals(errboxId, jq(".z-errorbox").attr("id"), "precondition: the same errorbox");
		List<String> tokens = Arrays.asList(describedBy(box).split("\\s+"));
		assertEquals(1, Collections.frequency(tokens, errboxId), "describedby=" + tokens);
		assertEquals(new HashSet<>(tokens).size(), tokens.size(), "duplicate token: " + tokens);

		getActions().sendKeys(Keys.END, Keys.BACK_SPACE, "12", Keys.TAB).perform();
		waitResponse();
		assertFalse(jq(".z-errorbox").exists(), "precondition: the error is cleared");
		assertEquals("", describedBy(box));
	}

	@Test
	public void coachmarkKeepsTargetDescribedbyTokens() {
		connect();
		waitResponse();
		if (!isA11yLoaded())
			return;

		JQuery box = jq("$coachTargetMask");
		String kept = "authored-id " + box.attr("id") + "-hint";
		assertEquals(kept, describedBy(box), "precondition: the authored id and the format hint");
		scrollIntoView(jq("$coachOpenBtn"));
		click(jq("$coachOpenBtn"));
		waitResponse();
		assertEquals(kept + " " + jq("$maskCoachmark").attr("id"), describedBy(box),
				"opening the coachmark must add to the ids, not replace them");

		click(jq(".z-coachmark-close"));
		waitResponse();
		assertEquals(kept, describedBy(box), "closing the coachmark must remove only its own id");
	}

	private String describedBy(JQuery box) {
		return getEval("document.getElementById('" + box.attr("id") + "').getAttribute('aria-describedby') || ''").trim();
	}

	private boolean isA11yLoaded() {
		return !Boolean.parseBoolean(getEval("typeof msgza11y === 'undefined'"));
	}
}
