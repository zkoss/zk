/* F50_0000001Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 14 15:15:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_0000001Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		click(jq("$modalBtn"));
		waitResponse(true);
		Assertions.assertTrue(jq("$hideBtn:focus").exists());

		click(jq("$invalidateBtn"));
		waitResponse(true);
		Assertions.assertTrue(jq("$hideBtn:focus").exists());

		click(jq("$hideBtn"));
		waitResponse(true);

		click(jq("$newBtn"));
		waitResponse(true);
		Assertions.assertTrue(jq("@textbox:focus").exists());
	}
}
