/* B96_ZK_5162Test.java

	Purpose:
		
	Description:
		
	History:
		2:34 PM 2023/12/22, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.NoAlertPresentException;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5162Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		try {
			driver.switchTo().alert();
			fail("Should not be here");
		} catch (NoAlertPresentException ex) {
			// ignore
		}
		assertEquals("<script>alert('XSS')</script>", jq(".z-grid-emptybody-content").text());
		assertEquals("<script>alert('XSS')</script>", jq(".z-listbox-emptybody-content").text());
	}
}

