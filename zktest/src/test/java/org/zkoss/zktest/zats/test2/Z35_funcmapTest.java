/* Z35_funcmapTest.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 16:13:49 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Z35_funcmapTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertEquals("'Correct'", jq(".z-label").eq(1).text().trim());
	}
}
