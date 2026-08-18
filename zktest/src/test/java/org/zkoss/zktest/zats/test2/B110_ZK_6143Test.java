/* B110_ZK_6143Test.java

        Purpose:

        Description:

        History:
                Tue Aug 18 11:40:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6143Test extends WebDriverTestCase {

	private static final String BEGIN = "$drb .z-daterangebox-begin";

	private static final String END = "$drb .z-daterangebox-end";

	private static final String INVALID = "z-daterangebox-invalid";

	/** @invalidBorderColor, spaces stripped so the compare is layout-independent. */
	private static final String INVALID_BORDER = "rgb(255,64,81)";

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	/** Commit text the way a blur does, without focusing: focusing opens the
	 *  calendar, which would cover the buttons below. */
	private void commitText(String selector, String text) {
		js().executeScript(
				"var inp = jq(\"" + selector + "\")[0];"
				+ "inp.value = arguments[0];"
				+ "inp.dispatchEvent(new Event('change', {bubbles: true}));",
				text);
		waitResponse();
	}

	/** Retype without a `change` event, so nothing is committed. */
	private void retypeWithoutCommitting(String selector, String text) {
		js().executeScript(
				"jq(\"" + selector + "\")[0].value = arguments[0];", text);
	}

	/** Redraw the box in place, keeping the same widget. */
	private void rerenderBox() {
		js().executeScript("zk.Widget.$('$drb').rerender();");
		waitResponse();
	}

	/** How many errorboxes the browser is actually laying out. A destroyed bubble
	 *  leaves no node, but a bubble left on screen has client rects — asserting
	 *  only on the node would miss a hidden-but-present one either way. */
	private int visibleErrorboxes() {
		return Integer.parseInt(getEval("(function () {"
				+ "var n = 0;"
				+ "document.querySelectorAll('.z-errorbox').forEach(function (e) {"
				+ "  if (e.getClientRects().length) n++;"
				+ "});"
				+ "return n;"
				+ "})()"));
	}

	/** The errorbox opens on a 50ms timer, so it is in the DOM before it is laid out. */
	private void waitErrorboxShown() {
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> visibleErrorboxes() >= 1);
	}

	/** Click the bubble's own X — "I have read it", not "the value is fine". */
	private void clickErrorboxClose() {
		waitErrorboxShown();
		assertTrue(jq(".z-errorbox-close").exists(),
				"precondition: the errorbox must carry a close button to click");
		click(jq(".z-errorbox-close"));
		waitResponse();
	}

	private void assertNoErrorbox(String message) {
		assertFalse(jq(".z-errorbox").exists(), message);
		assertEquals(0, visibleErrorboxes(),
				message + " — and none may be left laid out on screen");
	}

	private void seedRejectedText() {
		commitText(BEGIN, "not-a-date");
		assertTrue(jq("$drb").hasClass(INVALID),
				"precondition: unparseable text must mark the box invalid");
	}

	/** A format repaint must keep the text the user still has to fix. */
	@Test
	public void testFormatChangeKeepsRejectedText() {
		connect();
		waitResponse();

		type(jq(BEGIN), "not-a-date");
		waitResponse();
		// Focusing an input opens the calendar, which would cover the buttons.
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		assertTrue(jq("$drb").hasClass(INVALID),
				"precondition: unparseable text must mark the box invalid");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals("not-a-date", jq(BEGIN).val(),
				"a repaint must not discard the text the user still has to fix");
		assertTrue(jq("$drb").hasClass(INVALID),
				"the invalid mark must survive the repaint");
	}

	/** A repaint must read the input, not the remembered text. The correction is
	 *  written in the format the button switches to, which the repaint parses. */
	@Test
	public void testFormatChangeKeepsUncommittedCorrection() {
		connect();
		waitResponse();
		seedRejectedText();

		retypeWithoutCommitting(BEGIN, "2026-08-07");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals("2026-08-07", jq(BEGIN).val(),
				"a repaint must not put older rejected text back over the correction");
		assertFalse(jq("$drb").hasClass(INVALID),
				"the mark must not be re-raised on text the user has already fixed");
		assertFalse(jq(".z-errorbox").exists(),
				"and the errorbox must not come back with it");
	}

	/** A redraw must re-establish the rejected text and its mark. */
	@Test
	public void testRedrawKeepsRejectedTextAndMark() {
		connect();
		waitResponse();
		seedRejectedText();

		rerenderBox();

		assertEquals("not-a-date", jq(BEGIN).val(),
				"a redraw must not silently discard the rejected text");
		assertTrue(jq("$drb").hasClass(INVALID),
				"a redraw must not silently discard the invalid mark");
		// unbind_ destroys the errorbox, so the redraw has to raise it again
		assertTrue(jq(".z-errorbox").exists(),
				"a redraw must keep the errorbox with the mark");
	}

	/** The rejected text must not stick: a correction clears the mark for good. */
	@Test
	public void testCorrectingTheTextClearsItForGood() {
		connect();
		waitResponse();
		seedRejectedText();
		rerenderBox();

		commitText(BEGIN, "2026/08/07");
		assertEquals("2026/08/07", jq(BEGIN).val(),
				"the corrected value must be accepted");
		assertFalse(jq("$drb").hasClass(INVALID),
				"correcting the text must clear the invalid mark");

		rerenderBox();
		assertEquals("2026/08/07", jq(BEGIN).val(),
				"a redraw after the correction must repaint the corrected value");
		assertFalse(jq("$drb").hasClass(INVALID),
				"the mark must not come back after the correction");
	}

	/** A server-pushed value replaces the rejected text and clears the mark. */
	@Test
	public void testServerPushedValueReplacesRejectedText() {
		connect();
		waitResponse();
		seedRejectedText();

		click(jq("$btnSetBegin"));
		waitResponse();

		assertEquals("2026/08/07", jq(BEGIN).val(),
				"a server-pushed value must replace the rejected text");
		assertFalse(jq("$drb").hasClass(INVALID),
				"a server-pushed value must clear the mark the rejected text earned");
	}

	/** Parse-validity is per-field while the mark is whole-widget: each side keeps
	 *  its own text, and one side's fix must not clear the other's mark. */
	@Test
	public void testRedrawKeepsBothSidesRejectedText() {
		connect();
		waitResponse();
		commitText(BEGIN, "not-a-date");
		commitText(END, "also-not-a-date");

		rerenderBox();

		assertEquals("not-a-date", jq(BEGIN).val(),
				"the begin input must keep its own rejected text");
		assertEquals("also-not-a-date", jq(END).val(),
				"the end input must keep its own rejected text");
		assertTrue(jq("$drb").hasClass(INVALID),
				"the mark must survive with both sides still unparseable");

		click(jq("$btnSetBegin"));
		waitResponse();
		assertEquals("2026/08/07", jq(BEGIN).val(),
				"the pushed value must replace only the begin side's rejected text");
		assertEquals("also-not-a-date", jq(END).val(),
				"the end side's rejected text must be left alone");
		assertTrue(jq("$drb").hasClass(INVALID),
				"the mark must stay while the end input is still unparseable");
	}

	/** Unparseable text must raise the standard errorbox. The client defers
	 *  onChange while it is unparseable, so no server-side path can raise one. */
	@Test
	public void testUnparseableInputShowsErrorbox() {
		connect();
		waitResponse();

		// control: so a failure below is the daterangebox, not the type/blur harness
		type(jq("$db").find("input"), "not-a-date");
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
				"control: datebox must raise the standard errorbox on unparseable input");

		type(jq(BEGIN), "not-a-date");
		waitResponse();
		assertTrue(jq(".z-errorbox").text().contains("Invalid range"),
				"daterangebox must raise the standard errorbox on unparseable input, was: "
						+ jq(".z-errorbox").text());
		assertTrue(jq("$drb").hasClass(INVALID),
				"the red border must still accompany the errorbox");
	}

	/** The errorbox must clear once the text parses again. */
	@Test
	public void testErrorboxClearsWhenInputParsesAgain() {
		connect();
		waitResponse();

		type(jq(BEGIN), "not-a-date");
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
				"precondition: unparseable input must raise the errorbox");

		type(jq(BEGIN), "2026/07/20");
		waitResponse();
		assertFalse(jq(".z-errorbox").exists(),
				"the errorbox must clear once the input parses again");
	}

	/** Emptying the field is valid input, so it must clear the errorbox too. */
	@Test
	public void testClearingInputClearsErrorbox() {
		connect();
		waitResponse();

		type(jq(BEGIN), "not-a-date");
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
				"precondition: unparseable input must raise the errorbox");

		// committed without focus: focusing opens the calendar, which swallows the
		// select-all + backspace type("") uses to empty a field
		commitText(BEGIN, "");
		assertEquals("", jq(BEGIN).val(),
				"precondition: the input must really be empty");
		assertFalse(jq(".z-errorbox").exists(),
				"emptying the input is valid input, so the errorbox must clear");
	}

	/** Clearing is an edit like a correction: a repaint that arrives before the
	 *  clearing is committed must read the empty input, not restore what it held. */
	@Test
	public void testFormatChangeKeepsUncommittedClearing() {
		connect();
		waitResponse();
		seedRejectedText();

		retypeWithoutCommitting(BEGIN, "");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals("", jq(BEGIN).val(),
				"a repaint must not put the rejected text back over a cleared input");
		assertFalse(jq("$drb").hasClass(INVALID),
				"an empty input is valid input, so the mark must not survive the repaint");
		assertFalse(jq(".z-errorbox").exists(),
				"and the errorbox must go with the mark");

		rerenderBox();
		assertEquals("", jq(BEGIN).val(),
				"the dropped rejected text must not come back on a later redraw");
		assertFalse(jq("$drb").hasClass(INVALID),
				"nor may the mark it earned");
	}

	/** Clearing one side must leave the other side's rejected text and the
	 *  whole-widget mark it still earns alone. */
	@Test
	public void testUncommittedClearingKeepsSiblingRejectedText() {
		connect();
		waitResponse();
		commitText(BEGIN, "not-a-date");
		commitText(END, "also-not-a-date");

		retypeWithoutCommitting(BEGIN, "");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals("", jq(BEGIN).val(),
				"the cleared input must stay cleared through the repaint");
		assertEquals("also-not-a-date", jq(END).val(),
				"the end side's rejected text must survive the repaint");
		assertTrue(jq("$drb").hasClass(INVALID),
				"the mark must stay while the end input is still unparseable");
	}

	/** Fixing one input must not drop the feedback while the sibling is still bad. */
	@Test
	public void testErrorboxSurvivesWhileSiblingInputStillInvalid() {
		connect();
		waitResponse();

		type(jq(BEGIN), "not-a-date");
		waitResponse();
		type(jq(END), "also-not-a-date");
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
				"precondition: two unparseable inputs must raise the errorbox");

		type(jq(BEGIN), "2026/07/20");
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
				"the errorbox must survive while the end input still holds bad text");

		type(jq(END), "2026/07/25");
		waitResponse();
		assertFalse(jq(".z-errorbox").exists(),
				"the errorbox must clear once both inputs parse");
	}

	/** The mark has to be visible, not merely present: asserting the class alone
	 *  passes while the rule paints with a token the shipped profiles set to
	 *  `transparent`, which is what the box actually did. */
	@Test
	public void testInvalidMarkPaintsAVisibleBorder() {
		connect();
		waitResponse();

		String pristine = jq("$drb").css("border-top-color");
		seedRejectedText();

		String invalid = jq("$drb").css("border-top-color");
		assertFalse(invalid.replace(" ", "").contains("rgba(0,0,0,0)"),
				"the invalid border must be painted, not transparent, was: " + invalid);
		assertNotEquals(pristine, invalid,
				"the invalid border must differ from the valid one, both were: " + invalid);
	}

	/** A server-side rejection has to raise the same pair as a client-side parse
	 *  failure. `zk.AuCmd0.wrongValue` routes through the widget's
	 *  setErrorMessage; without it the fallback opens the errorbox alone. */
	@Test
	public void testServerRejectionPaintsTheInvalidBorder() {
		connect();
		waitResponse();

		String pristine = jq("$drb").css("border-top-color");

		click(jq("$btnReject"));
		waitResponse();

		assertTrue(jq(".z-errorbox").exists(),
				"a server-side WrongValueException must open the errorbox");
		assertTrue(jq("$drb").hasClass(INVALID),
				"and must mark the box invalid, as a client-side parse failure does");
		String invalid = jq("$drb").css("border-top-color");
		assertEquals(INVALID_BORDER, invalid.replace(" ", ""),
				"the invalid border must be painted while the errorbox shows, was: " + invalid);
		assertNotEquals(pristine, invalid,
				"the invalid border must differ from the valid one, both were: " + invalid);
	}

	/** A server-side clear has to take the border away with the errorbox, or the
	 *  box stays red with nothing on screen naming the problem. */
	@Test
	public void testServerClearRemovesBorderWithErrorbox() {
		connect();
		waitResponse();

		String pristine = jq("$drb").css("border-top-color");

		click(jq("$btnReject"));
		waitResponse();
		assertTrue(jq("$drb").hasClass(INVALID),
				"precondition: a server-side rejection must mark the box invalid");

		click(jq("$btnClear"));
		waitResponse();

		assertFalse(jq(".z-errorbox").exists(),
				"a server-side clear must close the errorbox");
		assertFalse(jq("$drb").hasClass(INVALID),
				"and must drop the invalid mark with it, not leave a red border behind");
		String cleared = jq("$drb").css("border-top-color");
		assertEquals(pristine, cleared,
				"the border must go back to the valid colour, was: " + cleared);
	}

	/** Control for the two below: CE's own close button, on the plain datebox.
	 *  `zul.inp.InputWidget.clearErrorMessage(true, true)` destroys the bubble and
	 *  keeps the invalid mark — the contract the daterangebox has to match. A
	 *  failure here is the harness (selector, click, timing), not the component. */
	@Test
	public void testErrorboxCloseButtonControlOnDatebox() {
		connect();
		waitResponse();

		type(jq("$db").find("input"), "not-a-date");
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
				"precondition: the datebox must raise an errorbox on unparseable input");
		assertTrue(jq("$db").find("input").hasClass("z-datebox-invalid"),
				"precondition: and mark its input invalid");

		clickErrorboxClose();

		assertNoErrorbox("the close button must destroy the datebox's errorbox");
		assertTrue(jq("$db").find("input").hasClass("z-datebox-invalid"),
				"dismissing the bubble is not a correction, so the mark must stay");
	}

	/** The close button on a client-side parse failure. It has to drop the bubble
	 *  even though the input still holds unparseable text — re-deriving the mark
	 *  from that text keeps it raised, so a close that only re-derives is inert. */
	@Test
	public void testErrorboxCloseDropsBubbleAndKeepsBorderOnUnparseableText() {
		connect();
		waitResponse();

		String pristine = jq("$drb").css("border-top-color");
		commitText(BEGIN, "not-a-date");
		assertTrue(jq(".z-errorbox").exists(),
				"precondition: unparseable input must raise the errorbox");
		assertTrue(jq("$drb").hasClass(INVALID),
				"precondition: and mark the box invalid");

		clickErrorboxClose();

		assertNoErrorbox("the close button must drop the bubble, not leave it on screen");
		assertEquals("not-a-date", jq(BEGIN).val(),
				"dismissing the bubble must not touch the text the user has to fix");
		assertTrue(jq("$drb").hasClass(INVALID),
				"and the mark must stay while that text is still unparseable");
		String border = jq("$drb").css("border-top-color");
		assertEquals(INVALID_BORDER, border.replace(" ", ""),
				"the red border must still be painted, was: " + border);
		assertNotEquals(pristine, border,
				"the border must still differ from the valid one, both were: " + border);
	}

	/** Same button after a server-side rejection, where nothing is unparseable:
	 *  clearing the mark as well would leave the box looking accepted while the
	 *  server still holds the old value. */
	@Test
	public void testErrorboxCloseKeepsBorderAfterServerRejection() {
		connect();
		waitResponse();

		String pristine = jq("$drb").css("border-top-color");
		click(jq("$btnReject"));
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(),
				"precondition: a server-side rejection must raise the errorbox");
		assertTrue(jq("$drb").hasClass(INVALID),
				"precondition: and mark the box invalid");

		clickErrorboxClose();

		assertNoErrorbox("the close button must drop the bubble a server rejection raised");
		assertTrue(jq("$drb").hasClass(INVALID),
				"the mark must stay: the server still rejects the value it holds");
		String border = jq("$drb").css("border-top-color");
		assertEquals(INVALID_BORDER, border.replace(" ", ""),
				"the red border must still be painted, was: " + border);
		assertNotEquals(pristine, border,
				"the border must still differ from the valid one, both were: " + border);
	}

	/** A redraw re-raises the bubble from scratch, so it has to remember WHICH
	 *  message raised it. With unparseable text in an input AND a server-pushed
	 *  reason, re-deriving from the text alone downgrades the server's reason to
	 *  the generic one — the only text on screen naming the real problem. */
	@Test
	public void testRedrawKeepsTheServerMessageOverTheGenericOne() {
		connect();
		waitResponse();

		commitText(BEGIN, "not-a-date");
		assertTrue(jq(".z-errorbox").text().contains("Invalid range"),
				"precondition: the parse failure raises the generic message, was: "
						+ jq(".z-errorbox").text());

		click(jq("$btnWrongValue"));
		waitResponse();
		assertTrue(jq(".z-errorbox").text().contains("Outside the booking window"),
				"precondition: the server's message must replace the generic one, was: "
						+ jq(".z-errorbox").text());

		rerenderBox();

		assertTrue(jq(".z-errorbox").exists(),
				"the redraw must re-raise the errorbox");
		String text = jq(".z-errorbox").text();
		assertTrue(text.contains("Outside the booking window"),
				"the redraw must re-raise the server's own message, was: " + text);
		assertFalse(text.contains("Invalid range"),
				"and must not downgrade it to the generic one, was: " + text);
		assertTrue(jq("$drb").hasClass(INVALID),
				"the mark must survive the redraw with it");
	}

	/** A server rejection populates no rejected text, so a redraw that keys only
	 *  off that text drops the bubble entirely and leaves the red border alone on
	 *  screen with nothing naming the problem. */
	@Test
	public void testRedrawKeepsTheServerErrorbox() {
		connect();
		waitResponse();

		click(jq("$btnReject"));
		waitResponse();
		waitErrorboxShown();
		String before = jq(".z-errorbox").text();
		assertFalse(before.isEmpty(),
				"precondition: the server rejection must put a message on screen");
		assertEquals("", jq(BEGIN).val(),
				"precondition: nothing unparseable was typed, so no rejected text exists");

		rerenderBox();

		assertTrue(jq(".z-errorbox").exists(),
				"a redraw must keep the bubble a server rejection raised, not just the border");
		assertEquals(before, jq(".z-errorbox").text(),
				"and must re-raise the same message");
		assertTrue(jq("$drb").hasClass(INVALID),
				"the mark must survive the redraw too");
		String border = jq("$drb").css("border-top-color");
		assertEquals(INVALID_BORDER, border.replace(" ", ""),
				"the red border must still be painted after the redraw, was: " + border);
	}
}
