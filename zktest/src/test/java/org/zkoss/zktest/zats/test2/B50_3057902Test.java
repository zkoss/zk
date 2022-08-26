/* B50_3057902Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 12:08:41 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_3057902Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button:eq(0)"));
		waitResponse();

		click(jq(".z-button:eq(1)"));
		waitResponse();

		Assertions.assertTrue(jq(".z-apply-loading:contains(show the msg)").exists());
	}
}
