/* TimeboxTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 14:15:42 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author rudyhuang
 */
public class TimeboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@timebox"));
		waitResponse();

		getActions().sendKeys(Keys.UP, Keys.UP, Keys.UP, Keys.TAB).perform();
		verifyA11y();
	}
}
