/* IHtmlBasedComponentTest.java

	Purpose:
		
	Description:
		
	History:
		12:26 PM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.sul.IHtmlBasedComponent;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IHtmlBasedComponent} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/HtmlBasedComponent">HtmlBasedComponent</a>,
 * if any.
 * @see IHtmlBasedComponent
 * @author jumperchen
 */
public class IHtmlBasedComponentTest extends WebDriverTestCase {
	@Test
	public void testClickAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/click");
		Actions actions = getActions();
		click(jq("@button"));
		waitResponse();
		String label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("which=1"));
		assertTrue(label.contains("altKey=false"));
		assertTrue(label.contains("shiftKey=false"));
		assertTrue(label.contains("ctrlKey=false"));
		assertTrue(label.contains("metaKey=false"));

		actions.keyDown(Keys.ALT).click(toElement(jq("@button"))).keyUp(Keys.ALT).perform();
		waitResponse();

		label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("which=1"));
		assertTrue(label.contains("altKey=true"));
		assertTrue(label.contains("shiftKey=false"));
		assertTrue(label.contains("ctrlKey=false"));
		assertTrue(label.contains("metaKey=false"));

		actions.keyDown(Keys.SHIFT).keyDown(Keys.META)
				.click(toElement(jq("@button"))).keyUp(Keys.SHIFT).keyUp(Keys.META).perform();

		waitResponse();

		label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("which=1"));
		assertTrue(label.contains("altKey=false"));
		assertTrue(label.contains("shiftKey=true"));
		assertTrue(label.contains("ctrlKey=false"));
		assertTrue(label.contains("metaKey=true"));
	}

	@Test
	public void testDoubleClickAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/doubleClick");
		dblClick(jq("@button"));
		waitResponse();
		String label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("which=1"));
		assertTrue(label.contains("altKey=false"));
		assertTrue(label.contains("shiftKey=false"));
		assertTrue(label.contains("ctrlKey=false"));
		assertTrue(label.contains("metaKey=false"));
	}

	@Test
	public void testRightClickAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/rightClick");
		rightClick(jq("@button"));
		waitResponse();
		String label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("which=3"));
		assertTrue(label.contains("altKey=false"));
		assertTrue(label.contains("shiftKey=false"));
		assertTrue(label.contains("ctrlKey=false"));
		assertTrue(label.contains("metaKey=false"));
	}

	@Test
	public void testMouseOverAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/mouseOver");
		mouseOver(jq("@button"));
		waitResponse();
		String label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("which=0"));
		assertTrue(label.contains("altKey=false"));
		assertTrue(label.contains("shiftKey=false"));
		assertTrue(label.contains("ctrlKey=false"));
		assertTrue(label.contains("metaKey=false"));
	}

	@Test
	public void testMouseOutAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/mouseOut");
		mouseOver(jq("@button"));

		// trigger mouseOut
		getActions().moveByOffset(500, 500).perform();
		waitResponse();
		String label = jq("$msg").text();
		assertNotEquals("", label);
		assertTrue(label.contains("which=0"));
		assertTrue(label.contains("altKey=false"));
		assertTrue(label.contains("shiftKey=false"));
		assertTrue(label.contains("ctrlKey=false"));
		assertTrue(label.contains("metaKey=false"));
	}

	@Test
	public void testKeysAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/key");

		// onOK
		getActions().sendKeys(toElement(jq("$inp")), Keys.ENTER).perform();
		waitResponse();
		String label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("keyCode=13"));

		// onCancel
		getActions().sendKeys(toElement(jq("$inp")), Keys.ESCAPE).perform();
		waitResponse();
		label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("keyCode=27"));
	}

	@Test
	public void testAfterSizeAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/afterSize");
		waitResponse();
		String label = jq("$msg").text();
		assertNotNull(label);
		assertTrue(label.contains("width=300"));
		assertTrue(label.contains("height=300"));
	}

	@Test
	public void testDropAction() {
		connect("/base_components/ihtmlbasedcomponent/actions/drop");
		Actions actions = getActions();
		JQuery div1 = jq("@label:contains(div 0)");
		JQuery div2 = jq("@label:contains(div 1)");
		JQuery div3 = jq("@label:contains(div 2)");
		JQuery div4 = jq("@label:contains(div 3)");

		assertTrue(div1.exists());
		assertTrue(div2.exists());
		assertTrue(div3.exists());
		assertTrue(div4.exists());

		assertEquals(0, div1.offsetLeft());
		assertEquals(0, div1.offsetTop());
		assertEquals(90, div2.offsetLeft());
		assertEquals(0, div2.offsetTop());
		assertEquals(0, div3.offsetLeft());
		assertEquals(90, div3.offsetTop());
		assertEquals(90, div4.offsetLeft());
		assertEquals(90, div4.offsetTop());

		actions.dragAndDrop(toElement(div1), toElement(div4)).perform();
		waitResponse();
		assertEquals(90, div1.offsetLeft());
		assertEquals(90, div1.offsetTop());
		assertEquals(0, div4.offsetLeft());
		assertEquals(0, div4.offsetTop());

		actions.dragAndDrop(toElement(div1), toElement(div3)).perform();
		waitResponse();
		assertEquals(90, div3.offsetLeft());
		assertEquals(90, div3.offsetTop());
		assertEquals(0, div1.offsetLeft());
		assertEquals(90, div1.offsetTop());

	}

	@Test
	public void testSclass() {
		connect("/base_components/ihtmlbasedcomponent/sclass");
		assertTrue(jq(".sclass1.sclass2").exists());
	}

	@Test
	public void testZclass() {
		connect("/base_components/ihtmlbasedcomponent/zclass");
		assertTrue(jq(".mywin").exists());
		assertTrue(jq(".mywin-content").exists());
		assertFalse(jq(".z-window-content").exists());
	}

	@Test
	public void testFlex() {
		connect("/base_components/ihtmlbasedcomponent/flex");
		JQuery div = jq("$main");
		assertEquals(300, div.height());
		assertEquals(300, div.width());
	}

	@Test
	public void testFlexMin() {
		connect("/base_components/ihtmlbasedcomponent/flexMin");
		JQuery div = jq("$main");
		assertEquals(50, div.height(), 50);
		assertEquals(30, div.width(), 20);
	}

	@Test
	public void testFlexAndSize() {
		connect("/base_components/ihtmlbasedcomponent/flexAndSize");
		click(jq("@button:eq(0)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(Not allowed to set hflex and width at the same time)").exists());
		click(jq("@button:contains(OK)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(Not allowed to set vflex and height at the same time)").exists());
	}

	@Test
	public void testClientAction() {
		connect("/base_components/ihtmlbasedcomponent/clientAction");
		click(jq("@button"));
		waitResponse();
		// check whether there is animating element or not.
		assertTrue(jq(".z-label:animated").exists());
	}

	@Test
	public void testTabindex() {
		connect("/base_components/ihtmlbasedcomponent/tabindex");
		clickAt(jq("@textbox:eq(0)"), 3, 3);
		assertEquals("rgb(0, 147, 249)", jq("@textbox:eq(0)").css("borderColor"));
		getActions().sendKeys(Keys.TAB).perform();
		assertEquals("rgb(0, 147, 249)", jq("@textbox:eq(2)").css("borderColor"));
	}

	@Test
	public void testRenderdefer() {
		connect("/base_components/ihtmlbasedcomponent/renderdefer");
		assertFalse(jq(".z-label").exists());
		sleep(2000);
		assertTrue(jq(".z-label").exists());
		assertEquals("Test render defer", jq(".z-label").text());
	}

	@Test
	public void testTooltiptext() {
		connect("/base_components/ihtmlbasedcomponent/tooltiptext");
		assertEquals("tooltip", jq(".z-label").attr("title"));
	}

	@Test
	public void testZIndex() {
		connect("/base_components/ihtmlbasedcomponent/zIndex");
		getActions().moveByOffset(200, 200).click().perform();
		waitResponse();
		assertTrue(getZKLog().contains(" blue"));
	}
}
