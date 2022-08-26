/* Z35_tabbox_201Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 16:16:55 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Z35_tabbox_201Test extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			waitResponse();
			click(jq(".z-tab").eq(28));
			waitResponse();
			click(jq(".z-tab").eq(0));
			waitResponse();
			click(jq(".z-tab").eq(49));
			waitResponse();
			Assertions.assertFalse(hasError());
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}
}
