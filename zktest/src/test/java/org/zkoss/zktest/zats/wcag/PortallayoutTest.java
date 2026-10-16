/* PortallayoutTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Jul 06 17:31:41 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

public class PortallayoutTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
		sendKeys(jq("@panel").eq(0), Keys.SPACE); // the move popup
		waitResponse();
		verifyA11y();
		getActions().sendKeys(Keys.ARROW_RIGHT).perform(); // a column's positions
		waitResponse();
		verifyA11y();
	}
}
