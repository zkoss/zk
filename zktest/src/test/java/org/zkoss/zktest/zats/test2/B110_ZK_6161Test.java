/* B110_ZK_6161Test.java

        Purpose:

        Description:

        History:
                Thu Sep 24 15:18:55 CST 2026, Created by peakerlee

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
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6161Test extends WebDriverTestCase {

	private static final String BEGIN = "$drb .z-daterangebox-begin";

	private static final String END = "$drb .z-daterangebox-end";

	private static final String INVALID = "z-daterangebox-invalid";

	private static final String DATEBOX = "$db .z-datebox-input";

	private static final String NIGHTS_BEGIN = "$drbNights .z-daterangebox-begin";

	private static final String NIGHTS_END = "$drbNights .z-daterangebox-end";

	private static final String FORMAT_MESSAGE = "You must specify a date. Format: yyyy/MM/dd";

	private static final String NEW_FORMAT_MESSAGE = "You must specify a date. Format: yyyy-MM-dd";

	private static final String SERVER_REASON = "Outside the booking window";

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void typeAndCommit(String selector, String text) {
		// macOS Chrome 154: type()'s Cmd+A selects nothing, so clear first or the text is appended.
		js().executeScript("jq(\"" + selector + "\")[0].value = '';");
		type(jq(selector), text);
		waitResponse();
	}

	private void retypeWithoutCommitting(String selector, String text) {
		js().executeScript("jq(\"" + selector + "\")[0].value = arguments[0];", text);
	}

	private void rerenderBox() {
		js().executeScript("zk.Widget.$('$drb').rerender();");
		waitResponse();
	}

	private int visibleErrorboxes() {
		return Integer.parseInt(getEval("(function () {"
				+ "var n = 0;"
				+ "document.querySelectorAll('.z-errorbox').forEach(function (e) {"
				+ "  if (e.getClientRects().length) n++;"
				+ "});"
				+ "return n;"
				+ "})()"));
	}

	private String errorText() {
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(d -> visibleErrorboxes() >= 1);
		assertEquals(1, jq(".z-errorbox-content").length(),
				"exactly one errorbox may be open, or the text read back is ambiguous");
		return jq(".z-errorbox-content").text();
	}

	@Test
	public void testUnparseableBeginNamesTheFormat() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: unparseable text must mark the box invalid");

		assertEquals(FORMAT_MESSAGE, errorText(),
				"a typo must be told the expected format; \"Invalid range\" belongs to the range rules");
	}

	@Test
	public void testUnparseableEndNamesTheFormat() {
		connect();
		waitResponse();

		typeAndCommit(END, "abc");
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: unparseable text must mark the box invalid");

		assertEquals(FORMAT_MESSAGE, errorText(),
				"the end input must name the format too, not only the begin input");
	}

	@Test
	public void testOnErrorCarriesTheFormatMessage() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");

		assertEquals(FORMAT_MESSAGE, jq("$lblError").text(),
				"onError's message must be the text the user was shown");
	}

	@Test
	public void testMatchesTheDateboxMessage() {
		connect();
		waitResponse();

		typeAndCommit(DATEBOX, "abc");
		String dateboxMessage = errorText();

		typeAndCommit(DATEBOX, "");
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(d -> !jq(".z-errorbox").exists());

		typeAndCommit(BEGIN, "abc");

		assertEquals(dateboxMessage, errorText(),
				"the daterangebox must report an unparseable date the way the datebox does");
	}

	@Test
	public void testFormatChangeRenamesThePattern() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		assertEquals(FORMAT_MESSAGE, errorText(),
				"precondition: the parse failure names the current pattern");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals(NEW_FORMAT_MESSAGE, errorText(),
				"the errorbox must name the pattern the box parses now");
	}

	@Test
	public void testFormatChangeKeepsADismissedBubbleClosed() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		errorText();
		click(jq(".z-errorbox-close"));
		waitResponse();
		assertFalse(jq(".z-errorbox").exists(), "precondition: the X must close the errorbox");

		click(jq("$btnFormat"));
		waitResponse();

		assertFalse(jq(".z-errorbox").exists(),
				"a format change must not reopen an errorbox the user closed");
		assertTrue(jq("$drb").hasClass(INVALID), "the text still fails, so the mark stays");
	}

	@Test
	public void testFormatChangeKeepsTheServerReason() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		click(jq("$btnWrongValue"));
		waitResponse();
		assertEquals(SERVER_REASON, errorText(),
				"precondition: the server's reason must take over the errorbox");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals(SERVER_REASON, errorText(),
				"a format change must not replace the server's reason");
	}

	@Test
	public void testFormatChangeKeepsTheServerRejection() {
		connect();
		waitResponse();

		click(jq("$btnReject"));
		waitResponse();
		String reason = errorText();
		assertTrue(jq("$drb").hasClass(INVALID), "precondition: the rejection marks the box");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals(reason, errorText(),
				"a repaint must not clear an errorbox the box did not raise");
		assertTrue(jq("$drb").hasClass(INVALID), "nor the mark that goes with it");
	}

	@Test
	public void testRedrawAfterServerClearNamesTheFormat() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		click(jq("$btnWrongValue"));
		waitResponse();
		click(jq("$btnClear"));
		waitResponse();
		assertTrue(jq("$drb").hasClass(INVALID), "precondition: the text still fails, so the mark stays");

		rerenderBox();
		focus(jq(BEGIN));

		assertEquals(FORMAT_MESSAGE, errorText(),
				"the redraw must name the parse failure, not the reason the server withdrew");
	}

	@Test
	public void testFormatChangeRenamesARedrawnParseError() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		retypeWithoutCommitting(BEGIN, "2026/08/07");
		click(jq("$btnClear"));
		waitResponse();
		rerenderBox();
		focus(jq(BEGIN));
		assertEquals(FORMAT_MESSAGE, errorText(),
				"precondition: the redraw raises the parse failure again");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals(NEW_FORMAT_MESSAGE, errorText(),
				"the errorbox must name the pattern the box parses now");
	}

	@Test
	public void testFreshParseFailureSupersedesTheServerReason() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		click(jq("$btnWrongValue"));
		waitResponse();
		assertEquals(SERVER_REASON, errorText(), "precondition: the server's reason takes over the errorbox");
		typeAndCommit(BEGIN, "xyz");
		assertEquals(FORMAT_MESSAGE, errorText(), "precondition: a new parse failure names the format");

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals(NEW_FORMAT_MESSAGE, errorText(),
				"the new parse failure owns the errorbox, so a format change renames it");
		rerenderBox();
		focus(jq(BEGIN));
		assertEquals(NEW_FORMAT_MESSAGE, errorText(),
				"a redraw must not bring back the reason the parse failure replaced");
	}

	@Test
	public void testUnchangedFormatKeepsTheErrorbox() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		errorText();
		assertEquals("true",
				getEval("(window.zk6161Errorbox = zk.Widget.$('$drb').effects_.errMesg) != null"),
				"precondition: the open errorbox must be the box's own");

		click(jq("$btnShowTime"));
		waitResponse();

		assertEquals("true", getEval("window.zk6161Errorbox === zk.Widget.$('$drb').effects_.errMesg"),
				"an unchanged pattern must leave the open errorbox alone, not rebuild it");
		assertEquals(FORMAT_MESSAGE, errorText(), "and it still names the same pattern");
	}

	@Test
	public void testFormatChangeThatParsesRetractsTheMessage() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "2026-08-07");
		assertEquals(FORMAT_MESSAGE, errorText(), "precondition: the text is rejected under yyyy/MM/dd");

		click(jq("$btnFormat"));
		waitResponse();

		assertFalse(jq("$drb").hasClass(INVALID), "precondition: the new pattern parses the text");
		assertFalse(jq(".z-errorbox").exists(), "the errorbox must stop telling the user to type yyyy/MM/dd");
	}

	@Test
	public void testRedrawKeepsANullServerReason() {
		connect();
		waitResponse();

		click(jq("$btnNullReason"));
		waitResponse();
		String shown = errorText();
		assertNotEquals(FORMAT_MESSAGE, shown, "precondition: a null reason is not the format message");

		rerenderBox();
		focus(jq(BEGIN));

		assertEquals(shown, errorText(), "a redraw must re-raise the server's reason as it was sent");
	}

	@Test
	public void testFormatChangeLeavesAClosedBubbleClosed() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		errorText();
		js().executeScript("zWatch.fireDown('onHide', zk.Widget.$('$drb'));");
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> visibleErrorboxes() == 0);

		click(jq("$btnFormat"));
		waitResponse();

		assertEquals(0, visibleErrorboxes(), "a format change must not reopen a bubble the user is not looking at");
		focus(jq(BEGIN));
		assertEquals(NEW_FORMAT_MESSAGE, errorText(), "returning to the input brings it back, naming the new pattern");
	}

	@Test
	public void testDismissedBubbleStaysDismissedAcrossARedraw() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "abc");
		errorText();
		click(jq(".z-errorbox-close"));
		waitResponse();

		rerenderBox();
		focus(jq(BEGIN));
		waitResponse();

		assertFalse(jq(".z-errorbox").exists(), "a dismissed bubble must not come back on a redraw or a focus");
		assertTrue(jq("$drb").hasClass(INVALID), "the text still fails, so the mark stays");
	}

	@Test
	public void testRedrawDropsAMarkOnlyUncommittedTextEarned() {
		connect();
		waitResponse();

		retypeWithoutCommitting(END, "2026/09/");
		click(jq("$btnClear"));
		waitResponse();
		assertTrue(jq("$drb").hasClass(INVALID), "precondition: the half-typed end input earns the mark");

		rerenderBox();

		assertFalse(jq("$drb").hasClass(INVALID), "the redraw discarded that text, so the mark must go");
		assertFalse(jq(".z-errorbox").exists(), "and no errorbox may be raised for it");
	}

	@Test
	public void testReversedRangeKeepsItsOwnMessage() {
		connect();
		waitResponse();

		typeAndCommit(NIGHTS_BEGIN, "2026/09/05");
		typeAndCommit(NIGHTS_END, "2026/09/01");

		assertEquals("Begin date must not be later than end date", errorText(),
				"a reversed range parses, so it must keep its own reason");
	}
}
