/* F95_ZK_4551Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 21 16:06:48 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_4551Test extends WebDriverTestCase {
	@BeforeEach
	public void setUp() {
		connect();
	}

	@Test
	public void testHover() {
		getActions().moveToElement(toElement(jq("@button:eq(2)"))).pause(2000).perform();
		checkPopupPosition();
	}

	@Test
	public void testClick() {
		click(jq("@button:eq(0)"));
		waitResponse();
		checkPopupPosition();

		rightClick(jq("@button:eq(1)"));
		waitResponse();
		checkPopupPosition();
	}

	private void checkPopupPosition() {
		Assertions.assertEquals(jq("@textbox").offsetLeft(), jq("@popup").offsetLeft(), 2);
	}
}
