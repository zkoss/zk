/* B80_ZK_3262Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 16:24:28 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B80_ZK_3262Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-calendar-cell"));
		waitResponse();
		Assertions.assertFalse(jq(".z-errorbox").exists());
	}
}
