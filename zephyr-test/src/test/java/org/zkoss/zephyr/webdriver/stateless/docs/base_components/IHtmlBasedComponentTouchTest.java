/* IHtmlBasedComponentTest.java

	Purpose:
		
	Description:
		
	History:
		12:26 PM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.zkoss.stateless.sul.IHtmlBasedComponent;
import org.zkoss.test.webdriver.TouchWebDriverTestCase;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of touch device unit test for {@link IHtmlBasedComponent} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/HtmlBasedComponent">HtmlBasedComponent</a>,
 * if any.
 *
 * @author jumperchen
 * @see IHtmlBasedComponent
 */
public class IHtmlBasedComponentTouchTest extends TouchWebDriverTestCase {
	@Test
	public void testSwipe() {
		connect("/base_components/ihtmlbasedcomponent/actions/swipe");
		waitResponse();
		Point source = new Point(40, 40);
		Point target = new Point(200, 40);
		swipe(source, target, 700);
		waitResponse();
		assertTrue(jq("$msg").text().contains("swipeDirection='right'"));
	}
}
