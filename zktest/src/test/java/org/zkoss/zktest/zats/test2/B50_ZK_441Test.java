/* B50_ZK_441Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 15:38:03 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_441Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@colorbox"));
		waitResponse();
		click(jq(".z-colorbox-pickericon"));
		waitResponse();
		click(jq(".z-colorbox-paletteicon"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
