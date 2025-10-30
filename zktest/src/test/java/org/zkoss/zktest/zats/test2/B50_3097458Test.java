/* B50_3097458Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 14:40:15 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3097458Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		rightClick(jq(".z-treerow"));
		waitResponse();

		Assertions.assertTrue(jq(".z-messagebox:contains(512)").exists());
	}
}
