/* NavbarTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 12:00:04 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

/**
 * @author rudyhuang
 */
public class NavbarTest extends WcagTestCase {
	@Test
	public void testNormalHorizontal() {
		connect();

		click(jq("@navbar:eq(0) @nav:contains(Item 1)"));
		waitResponse(true);
		getActions().sendKeys(Keys.RIGHT, Keys.SPACE).perform();
		waitResponse(true);
		verifyA11y();
	}

	@Test
	public void testNormalVertical() {
		connect();

		click(jq("@navbar:eq(1) @nav:contains(Item 1)"));
		waitResponse(true);
		getActions().sendKeys(Keys.DOWN, Keys.SPACE).perform();
		waitResponse(true);
		verifyA11y();
	}

	@Test
	public void testCollapsedHorizontal() {
		connect();

		getActions()
				.moveToElement(toElement(jq("@navbar:eq(2) @nav:contains(Item 1)")))
				.pause(300)
				.sendKeys(Keys.DOWN, Keys.SPACE)
				.perform();
		waitResponse(true);
		verifyA11y();
	}

	@Test
	public void testCollapsedVertical() {
		connect();

		getActions()
				.moveToElement(toElement(jq("@navbar:eq(3) @nav:contains(Item 1)")))
				.pause(300)
				.sendKeys(Keys.DOWN, Keys.SPACE)
				.perform();
		waitResponse(true);
		getActions().sendKeys(Keys.DOWN, Keys.SPACE).perform();
		waitResponse();
		verifyA11y();
	}
}
