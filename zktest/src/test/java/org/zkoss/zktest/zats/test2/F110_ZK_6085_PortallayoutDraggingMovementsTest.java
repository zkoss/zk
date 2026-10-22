/* F110_ZK_6085_PortallayoutDraggingMovementsTest.java

	Purpose:

	Description:

	History:
		Thu Oct 15 15:22:19 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class F110_ZK_6085_PortallayoutDraggingMovementsTest extends WebDriverTestCase {
	@Test
	public void anotherColumnOffersItsEnds() {
		connect();
		clickTitleBar("translate");
		Assertions.assertEquals(3, columns().length());
		Assertions.assertEquals("Todo (current)", columns().eq(0).text());
		Assertions.assertEquals("Doing", columns().eq(1).text());
		Assertions.assertEquals("Done", columns().eq(2).text());
		Assertions.assertFalse(positions().exists());

		click(columns().eq(1));
		waitResponse();
		Assertions.assertTrue(columns().eq(1).is(".z-portallayout-popup-selected"));
		Assertions.assertEquals(2, positions().length(), "Another column should offer its ends only");
		Assertions.assertEquals("Move to top", positions().eq(0).text());
		Assertions.assertEquals("Move to bottom", positions().eq(1).text());
		click(positions().eq(1));
		waitResponse();
		Assertions.assertEquals(3, widget("$doing").nChildren());
		Assertions.assertEquals("translate", widget("$doing").lastChild().id(), "Translate should move below News");
		Assertions.assertEquals(2, widget("$todo").nChildren());
		assertNoAnyError();
	}

	@Test
	public void thePopupIsAMenu() {
		connect();
		clickTitleBar("translate");
		JQuery menu = jq(".z-portallayout-popup-open .z-portallayout-popup-content").eq(0);
		Assertions.assertEquals("menu", menu.attr("role"));
		Assertions.assertEquals("Move Translate", menu.attr("aria-label"));
		Assertions.assertEquals("menuitem", columns().eq(1).attr("role"));
		Assertions.assertEquals("menu", columns().eq(1).attr("aria-haspopup"));
		Assertions.assertEquals("false", columns().eq(1).attr("aria-expanded"));

		click(columns().eq(1));
		waitResponse();
		JQuery submenu = jq(".z-portallayout-popup-open .z-portallayout-popup-content").eq(1);
		Assertions.assertEquals("true", columns().eq(1).attr("aria-expanded"), "The column whose positions are open should be expanded");
		Assertions.assertEquals(submenu.attr("id"), columns().eq(1).attr("aria-controls"));
		Assertions.assertEquals("menu", submenu.attr("role"));
		Assertions.assertEquals(columns().eq(1).attr("id"), submenu.attr("aria-labelledby"), "The positions should be named after their column");
		Assertions.assertEquals("menuitem", positions().eq(0).attr("role"));
	}

	@Test
	public void aPositionPreviewsWhereThePanelGoes() {
		connect();
		clickTitleBar("translate");
		getActions().moveToElement(toElement(columns().eq(2))).perform();
		waitResponse();
		Assertions.assertTrue(jq("$done").find(".z-panel-move-block").exists(), "Done is empty and takes the panel at once, so pointing at it should preview it there");
		getActions().moveToElement(toElement(columns().eq(1))).perform();
		waitResponse();
		Assertions.assertFalse(preview().exists(), "A click on Doing opens its positions, so pointing at it should preview nothing");
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		Assertions.assertFalse(preview().exists(), "Enter on Doing opens its positions too, so focusing it should preview nothing");

		click(columns().eq(0));
		waitResponse();
		Assertions.assertFalse(preview().exists(), "The panel's own column takes it nowhere, so nothing should be previewed");
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		Assertions.assertTrue(middleOf(preview()) <= jq("$calendar").offsetTop(), "Move to top should be previewed above Calendar");
		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_DOWN).perform();
		waitResponse();
		JQuery weather = jq("$weather");
		Assertions.assertTrue(middleOf(preview()) >= weather.offsetTop() + weather.outerHeight(), "Move to bottom should be previewed below Weather");

		getActions().moveToElement(toElement(positions().eq(0))).perform();
		waitResponse();
		Assertions.assertTrue(middleOf(preview()) <= jq("$calendar").offsetTop(), "The hovered position should be previewed");
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertFalse(preview().exists(), "Tab closes the popup from a position, which should remove the preview");
	}

	@Test
	public void upOrDownClosesTheOpenPositions() {
		connect();
		clickTitleBar("translate");
		click(columns().eq(1));
		waitResponse();
		Assertions.assertTrue(positions().exists(), "precondition: a click on Doing opens its positions");
		getActions().sendKeys(Keys.ARROW_DOWN).perform();
		waitResponse();
		Assertions.assertEquals("Done", jq(":focus").text());
		Assertions.assertFalse(positions().exists(), "Moving to another column should close the positions of the one before");
		Assertions.assertFalse(columns().eq(1).is(".z-portallayout-popup-selected"));
	}

	@Test
	public void escapeClosesOnlyThePositions() {
		connect();
		clickTitleBar("translate");
		click(columns().eq(1));
		waitResponse();
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		Assertions.assertEquals("Move to top", jq(":focus").text(), "precondition: Right moved the focus into the positions of Doing");
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		Assertions.assertFalse(positions().exists(), "Escape on a position should close only the positions, as in a submenu");
		Assertions.assertEquals("Doing", jq(":focus").text(), "The focus should go back to the column the positions belong to");
		Assertions.assertEquals("false", columns().eq(1).attr("aria-expanded"), "Doing should no longer be expanded");
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "A second Escape should close the popup");
		Assertions.assertTrue(jq("$translate").is(":focus"), "The focus should go back to the panel");
	}

	@Test
	public void keysMoveAPanelWithinItsColumn() {
		connect();
		sendKeys(jq("$weather"), Keys.SPACE);
		waitResponse();
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		Assertions.assertEquals("Move to top", jq(":focus").text(), "Right should open the positions at the first one");
		Assertions.assertEquals(2, positions().length(), "Weather is at the bottom, so only the moves up should be offered");
		Assertions.assertEquals("Move up", positions().eq(1).text());
		getActions().sendKeys(Keys.ARROW_LEFT).perform();
		waitResponse();
		Assertions.assertFalse(positions().exists(), "Left should close the positions");
		Assertions.assertEquals("Todo (current)", jq(":focus").text());
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assertions.assertEquals("Move to top", jq(":focus").text(), "Enter should open the positions too, as a click does");
		getActions().sendKeys(Keys.ARROW_DOWN, Keys.ENTER).perform();
		waitResponse();
		Assertions.assertEquals("weather", widget("$todo").firstChild().nextSibling().id(), "Weather should move above Translate");
		Assertions.assertTrue(jq("$weather").is(":focus"), "The moved panel should keep the focus");
		assertNoAnyError();
	}

	@Test
	public void theOwnColumnOffersAStepEitherWay() {
		connect();
		clickTitleBar("translate");
		click(columns().eq(0));
		waitResponse();
		Assertions.assertEquals(4, positions().length());
		Assertions.assertEquals("Move to top", positions().eq(0).text());
		Assertions.assertEquals("Move up", positions().eq(1).text());
		Assertions.assertEquals("Move down", positions().eq(2).text());
		Assertions.assertEquals("Move to bottom", positions().eq(3).text());
		click(positions().eq(2));
		waitResponse();
		Assertions.assertEquals("translate", widget("$todo").lastChild().id(), "Translate should move below Weather");
		assertNoAnyError();
	}

	@Test
	public void anEmptyColumnTakesThePanelAtOnce() {
		connect();
		clickTitleBar("calendar");
		click(columns().eq(2));
		waitResponse();
		Assertions.assertEquals("calendar", widget("$done").firstChild().id(), "An empty column should take Calendar at once");

		clickTitleBar("calendar");
		Assertions.assertEquals(2, columns().length(), "Calendar's own column has no position to move to");
		assertNoAnyError();
	}

	@Test
	public void aSecondClickOnTheTitleBarClosesThePopup() {
		connect();
		clickTitleBar("translate");
		clickTitleBar("translate");
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "A second click on the title bar should close the popup");
		clickTitleBar("translate");
		Assertions.assertTrue(jq(".z-portallayout-popup-open").exists(), "A third click should open it again");
		clickTitleBar("mail");
		Assertions.assertEquals("Doing (current)", columns().eq(1).text(), "A click on another panel's title bar should open its popup");
	}

	@Test
	public void theOwnRowOffersAStepEitherWay() {
		connect();
		clickTitleBar("middle");
		Assertions.assertEquals("Row 1 (current)", columns().eq(0).text());
		Assertions.assertEquals("Row 2", columns().eq(1).text());
		click(columns().eq(0));
		waitResponse();
		getActions().moveToElement(toElement(positions().eq(0))).perform();
		waitResponse();
		Assertions.assertTrue(preview().offsetLeft() + preview().outerWidth() / 2 <= jq("$left").offsetLeft(), "Move to start should be previewed left of Left");
		Assertions.assertEquals(4, positions().length());
		Assertions.assertEquals("Move to start", positions().eq(0).text());
		Assertions.assertEquals("Move left", positions().eq(1).text());
		Assertions.assertEquals("Move right", positions().eq(2).text());
		Assertions.assertEquals("Move to end", positions().eq(3).text());
		click(positions().eq(1));
		waitResponse();
		Assertions.assertEquals("middle", widget("$row1").firstChild().id(), "Middle should move to the start");
		assertNoAnyError();
	}

	@Test
	public void theDragGripOpensThePopup() {
		connect();
		click(jq("$untitled").find(".z-panel-drag-button"));
		waitResponse();
		Assertions.assertEquals("Only (current)", columns().eq(0).text());
		Assertions.assertEquals("Move to top", jq(":focus").text(), "With one column, the popup should open its positions at once");
		click(positions().eq(0));
		waitResponse();
		Assertions.assertEquals("untitled", widget("$only").firstChild().id(), "The untitled panel should move to the top");
		assertNoAnyError();
	}

	@Test
	public void aPanelThatDraggingCannotMoveHasNoPopup() {
		connect();
		sendKeys(jq("$locked"), Keys.SPACE);
		waitResponse();
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "Space should not open a popup");
		clickTitleBar("locked");
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "A click on the title bar should not open a popup");
	}

	@Test
	public void aMaximizedPanelHasNoPopup() {
		connect();
		click(jq("$mail").find(".z-panel-maximize"));
		waitResponse();
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "A header button should not open the popup");
		sendKeys(jq("$mail"), Keys.SPACE);
		waitResponse();
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "Dragging ignores a maximized panel, so Space should not open a popup");
		clickTitleBar("mail");
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "Dragging ignores a maximized panel, so a click should not open a popup");
		assertNoAnyError();
	}

	@Test
	public void aNestedPanelHasNoPopup() {
		connect();
		sendKeys(jq("$nested"), Keys.SPACE);
		waitResponse();
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "Dragging ignores a panel inside a portal panel, so Space should not open a popup");
		clickTitleBar("nested");
		Assertions.assertFalse(jq(".z-portallayout-popup-open").exists(), "Dragging ignores a panel inside a portal panel, so a click should not open a popup");
		assertNoAnyError();
	}

	private void clickTitleBar(String panel) {
		click(jq("$" + panel).find(".z-panel-header"));
		waitResponse();
	}

	private JQuery preview() {
		return jq(".z-portalchildren .z-panel-move-block");
	}

	private int middleOf(JQuery bar) {
		return bar.offsetTop() + bar.outerHeight() / 2;
	}

	private JQuery columns() {
		return jq(".z-portallayout-popup-open .z-portallayout-popup-content:first-child > li:visible");
	}

	private JQuery positions() {
		return jq(".z-portallayout-popup-open .z-portallayout-popup-content + .z-portallayout-popup-content > li");
	}
}
