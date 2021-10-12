/* F50_3014112Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 10:48:33 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3014112Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		new WebDriverWait(driver, 5).until(ExpectedConditions.alertIsPresent());
	}
}
