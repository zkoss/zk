/* IDateboxRichletTest.java

	Purpose:

	Description:

	History:
		Wed Mar 02 15:51:53 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.sul.IDatebox;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IDatebox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Datebox">Datebox</a>,
 * if any.
 *
 * @author katherine
 * @see IDatebox
 */
public class IDateboxRichletTest extends WebDriverTestCase {

	@Test
	public void buttonVisible() {
		connect("/input/iDatebox/buttonVisible");
		assertTrue(jq(".z-datebox-button.z-datebox-disabled").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-datebox-button").exists());
	}

	@Test
	public void closePopupOnTimezoneChange() {
		connect("/input/iDatebox/closePopupOnTimezoneChange");
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-datebox-timezone select"));
		waitResponse();
		click(jq(".z-datebox-timezone select option:eq(1)"));
		waitResponse();
		assertTrue(jq(".z-datebox-popup").isVisible());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-datebox-timezone select"));
		waitResponse();
		click(jq(".z-datebox-timezone select option:eq(0)"));
		waitResponse();
		assertFalse(jq(".z-datebox-popup").isVisible());
	}

	@Test
	public void defaultDateTime() {
		connect("/input/iDatebox/defaultDateTime");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertTrue(jq(".z-calendar-text:eq(0)").text().contains("Apr"));
		assertTrue(jq(".z-calendar-text:eq(1)").text().contains("1"));
	}

	@Test
	public void displayedTimeZones() {
		connect("/input/iDatebox/displayedTimeZones");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertEquals(2, jq(".z-datebox-timezone select option").length());
	}

	@Test
	public void format() {
		connect("/input/iDatebox/format");
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-calendar-selected"));
		waitResponse();
		assertTrue(jq(".z-datebox-input").val().contains("**"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-datebox-input").val().contains("---"));
	}

	@Test
	public void lenient() {
		connect("/input/iDatebox/lenient");
		click(jq(".z-datebox-input"));
		Actions act = getActions();
		act.sendKeys("01/2015").sendKeys(Keys.TAB).perform();
		waitResponse();
		assertFalse(jq(".z-errorbox").exists());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-input"));
		act.sendKeys(Keys.DELETE).sendKeys(Keys.BACK_SPACE).sendKeys(Keys.TAB);
		waitResponse();
		assertFalse(jq(".z-errorbox").exists());
	}

	@Test
	public void position() {
		connect("/input/iDatebox/position");
		JQuery box = jq(".z-datebox");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertEquals(box.offsetLeft() + box.width(), jq(".z-datebox-popup").offsetLeft(), 2);
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		assertEquals(box.offsetLeft(), jq(".z-datebox-popup").offsetLeft(), 2);
	}

	@Test
	public void selectLevel() {
		connect("/input/iDatebox/selectLevel");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertTrue(jq(".z-calendar-selected").text().length() > 2);
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		assertTrue(jq(".z-calendar-selected").text().length() <= 2);
	}

	@Test
	public void showTodayLink() {
		connect("/input/iDatebox/showTodayLink");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertTrue(jq(".z-calendar-today").exists());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		assertFalse(jq(".z-calendar-today").exists());
	}

	@Test
	public void strictDate() {
		connect("/input/iDatebox/strictDate");
		click(jq(".z-datebox-input"));
		waitResponse();
		Actions act = getActions();
		click(jq("@button"));
		waitResponse();
		WebElement input = toElement(jq(".z-datebox-input"));
		input.clear();
		click(jq(".z-datebox-input"));
		act.sendKeys("Oct 32, 2").sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(jq(".z-errorbox-open").exists());
	}

	@Test
	public void timeZonesReadonly() {
		connect("/input/iDatebox/timeZonesReadonly");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertEquals("disabled", jq("select").attr("disabled"));
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		assertEquals("null", jq("select").attr("disabled"));
	}

	@Test
	public void todayLinkLabel() {
		connect("/input/iDatebox/todayLinkLabel");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertEquals("today", jq(".z-calendar-today").text());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		assertEquals("go to today", jq(".z-calendar-today").text());
	}

	@Test
	public void weekOfYear() {
		connect("/input/iDatebox/weekOfYear");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertTrue(jq(".z-calendar-weekofyear").exists());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		assertFalse(jq(".z-calendar-weekofyear").exists());
	}

	@Test
	public void constraint() {
		connect("/input/iDatebox/constraint");
		click(jq(".z-datebox-button"));
		waitResponse();
		assertTrue(jq(".z-calendar-selected").prev().hasClass("z-calendar-disabled"));
		assertTrue(jq(".z-calendar-selected").next().hasClass("z-calendar-disabled"));
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		assertTrue(jq(".z-calendar-selected").prev().hasClass("z-calendar-disabled"));
		assertFalse(jq(".z-calendar-selected").next().hasClass("z-calendar-disabled"));
	}
}