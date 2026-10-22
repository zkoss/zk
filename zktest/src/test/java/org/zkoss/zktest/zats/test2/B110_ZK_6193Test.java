/* B110_ZK_6193Test.java

        Purpose:

        Description:

        History:
                Tue Oct 13 10:36:56 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6193Test extends WebDriverTestCase {

	/** Clicks the middle of the field, which toggles the popup. */
	private void clickField() {
		click(jq("$cas"));
		waitResponse(true);
	}

	private void clickItem(String text) {
		click(jq(".z-cascader-item:contains(\"" + text + "\")"));
		waitResponse();
	}

	/** Fires the button's onClick without a pointer press, whose mousedown would close the popup as an outside click. */
	private void fireOnServer(String buttonId) {
		getEval("(zk.Widget.$(jq('$" + buttonId + "')[0]).fire('onClick',{},{toServer:true}),'')");
		waitResponse();
	}

	private boolean isOpen() {
		return jq("$cas").hasClass("z-cascader-open");
	}

	/** The field's own icon; the popup's parent items carry the same z-cascader-icon class. */
	private JQuery fieldIcon() {
		return jq(widget("$cas").$n("icon"));
	}

	private List<String> highlighted() {
		List<String> texts = new ArrayList<>();
		for (JQuery item : jq(".z-cascader-selected"))
			texts.add(item.text());
		return texts;
	}

	private void expandJapanFromEmpty() {
		connect();
		waitResponse();
		clickField();
		clickItem("Japan");
		assertTrue(isOpen(), "clicking a parent must keep the popup open");
		assertEquals(2, jq(".z-cascader-cave").length(), "clicking Japan must open its children column");
	}

	/** The field, the server and the reopened popup all still show an empty selection. */
	private void assertNothingCommitted() {
		assertNothingCommitted("0");
	}

	/** The same, after the given number of onSelect events. */
	private void assertNothingCommitted(String onSelectCount) {
		assertFalse(isOpen(), "the popup must be closed");
		JQuery icon = fieldIcon();
		assertFalse(icon.hasClass("z-icon-times"), "an empty field must not show the clear icon");
		assertTrue(icon.hasClass("z-icon-caret-down"), "an empty field must show the dropdown icon");
		assertTrue(jq(".z-cascader-placeholder").isVisible(), "the placeholder must stay visible");
		assertEquals("none", jq("$sel").text(), "the server must still have no selected item");
		assertEquals(onSelectCount, jq("$count").text(), "no other onSelect may reach the server");

		clickField();
		assertTrue(isOpen(), "clicking the field must reopen the popup");
		assertEquals(List.of(), highlighted(), "reopening must not highlight the abandoned parent");
		assertEquals(1, jq(".z-cascader-cave").length(), "reopening must show only the first column");
	}

	@Test
	public void testOutsideClickDiscardsTheExpandedParent() {
		expandJapanFromEmpty();
		click(jq("$outside"));
		waitResponse();
		assertNothingCommitted();
	}

	@Test
	public void testEscapeDiscardsTheExpandedParent() {
		expandJapanFromEmpty();
		sendKeys(jq("$cas"), Keys.ESCAPE);
		waitResponse();
		assertNothingCommitted();
	}

	@Test
	public void testTabDiscardsTheExpandedParent() {
		expandJapanFromEmpty();
		sendKeys(jq("$cas"), Keys.TAB);
		waitResponse();
		assertNothingCommitted();
	}

	@Test
	public void testFieldClickDiscardsTheExpandedParent() {
		expandJapanFromEmpty();
		clickField();
		assertNothingCommitted();
	}

	/** While nothing is committed the icon is an arrow, so clicking it closes instead of clearing. */
	@Test
	public void testArrowIconClickDiscardsTheExpandedParent() {
		expandJapanFromEmpty();
		click(fieldIcon());
		waitResponse();
		assertNothingCommitted();
	}

	@Test
	public void testServerCloseDiscardsTheExpandedParent() {
		expandJapanFromEmpty();
		fireOnServer("serverClose");
		assertNothingCommitted();
	}

	@Test
	public void testAbandonedNavigationKeepsTheCommittedSelection() {
		connect();
		waitResponse();
		clickField();
		clickItem("Japan");
		clickItem("Tokyo");
		assertEquals("Japan/Tokyo", jq(".z-cascader-label").text());
		assertEquals("Tokyo", jq("$sel").text());

		clickField();
		clickItem("USA");
		assertTrue(jq(".z-cascader-item:contains(\"New York\")").isVisible(), "clicking USA must open its children column");
		click(jq("$outside"));
		waitResponse();

		assertFalse(isOpen(), "the popup must be closed");
		assertEquals("Japan/Tokyo", jq(".z-cascader-label").text(), "the field must keep the committed selection");
		assertTrue(fieldIcon().hasClass("z-icon-times"), "a committed selection keeps the clear icon");
		assertEquals("Tokyo", jq("$sel").text(), "the server must keep Tokyo");
		assertEquals("1", jq("$count").text(), "only the Tokyo pick may reach the server");

		clickField();
		assertEquals(List.of("Japan", "Tokyo"), highlighted(), "reopening must highlight the committed path, not USA");
		assertEquals(2, jq(".z-cascader-cave").length());
	}

	/** A selection the server pushes while a parent is expanded becomes the one a later close keeps. */
	@Test
	public void testServerSelectionReplacesTheNavigation() {
		expandJapanFromEmpty();
		fireOnServer("serverSelect");
		sendKeys(jq("$cas"), Keys.ESCAPE);
		waitResponse();

		assertFalse(isOpen(), "the popup must be closed");
		assertEquals("USA/New York", jq(".z-cascader-label").text());
		assertTrue(fieldIcon().hasClass("z-icon-times"), "the server's selection shows the clear icon");

		clickField();
		assertEquals(List.of("USA", "New York"), highlighted(), "reopening must highlight the server's selection");
	}

	/** Clearing with × commits the empty selection, so a later close does not restore Japan/Tokyo. */
	@Test
	public void testClearedSelectionStaysClearedAfterClose() {
		connect();
		waitResponse();
		clickField();
		clickItem("Japan");
		clickItem("Tokyo");
		assertTrue(fieldIcon().hasClass("z-icon-times"), "a committed selection shows the clear icon");

		click(fieldIcon());
		waitResponse();
		assertEquals("none", jq("$sel").text(), "clicking the clear icon must clear the server's selection");

		clickField();
		click(jq("$outside"));
		waitResponse();
		assertNothingCommitted("2");
	}

	/** Backspace, which the za11y augment maps to clear, has nothing to clear on an empty field. */
	@Test
	public void testBackspaceOnAnEmptyFieldFiresNoOnSelect() {
		connect();
		waitResponse();
		sendKeys(jq("$cas"), Keys.BACK_SPACE);
		waitResponse();
		assertEquals("0", jq("$count").text(), "no onSelect may reach the server");
		assertEquals("none", jq("$sel").text());
	}

	/** Backspace on a disabled field must not blank a selection the server still holds. */
	@Test
	public void testBackspaceOnADisabledFieldKeepsTheSelection() {
		connect();
		waitResponse();
		clickField();
		clickItem("Japan");
		clickItem("Tokyo");
		click(jq("$serverDisable"));
		waitResponse();
		assertTrue(jq("$cas").hasClass("z-cascader-disabled"), "the cascader must be disabled");

		sendKeys(jq("$cas"), Keys.BACK_SPACE);
		waitResponse();
		assertTrue(jq(".z-cascader-label").isVisible(), "the field must keep showing Japan/Tokyo");
		assertFalse(jq(".z-cascader-placeholder").isVisible(), "the placeholder must stay hidden");
		assertTrue(fieldIcon().hasClass("z-icon-times"), "the clear icon must stay");
		assertEquals("Tokyo", jq("$sel").text(), "the server must keep Tokyo");
		assertEquals("1", jq("$count").text(), "only the Tokyo pick may reach the server");
	}
}
