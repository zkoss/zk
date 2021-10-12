/* F95_ZK_4551Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 21 16:06:48 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_4551Test extends WebDriverTestCase {
	@Before
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
		Assert.assertEquals(jq("@textbox").offsetLeft(), jq("@popup").offsetLeft(), 2);
	}
}
