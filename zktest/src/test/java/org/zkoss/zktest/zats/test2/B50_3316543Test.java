/* B50_3316543Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 12:39:58 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3316543Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assertions.assertTrue(jq(".z-label:contains(mdminfile)").exists());
		Assertions.assertTrue(jq(".z-label:contains(wtbinfile)").exists());
	}
}
