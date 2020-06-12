/* CalendarTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 10:31:45 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author rudyhuang
 */
public class CalendarTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();

		click(jq("a:contains(Today)"));
		waitResponse();
		sendKeys(jq("@calendar:last"), Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP);
		waitResponse();

		verifyA11y();
	}
}
