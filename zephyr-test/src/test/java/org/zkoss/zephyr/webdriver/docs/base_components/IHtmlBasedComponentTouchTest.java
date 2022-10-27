/* IHtmlBasedComponentTest.java

	Purpose:
		
	Description:
		
	History:
		12:26 PM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.touch.TouchActions;

import org.zkoss.stateless.zpr.IHtmlBasedComponent;
import org.zkoss.test.webdriver.TouchWebDriverTestCase;

/**
 * A set of touch device unit test for {@link IHtmlBasedComponent} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/HtmlBasedComponent">HtmlBasedComponent</a>,
 * if any.
 * @see IHtmlBasedComponent
 * @author jumperchen
 */
public class IHtmlBasedComponentTouchTest extends TouchWebDriverTestCase {

	@Test
	public void testSwipe() {
		connect("/base_components/ihtmlbasedcomponent/actions/swipe");
		TouchActions touchActions = getTouchActions();
		touchActions.scroll(300,300)
				.down(40, 40).move(200, 40).up(200,40).perform();
		waitResponse();
		assertTrue(jq("$msg").text().contains("swipeDirection='right'"));
	}
}
