/* B90_ZK_4297Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 30 16:14:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.Color;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4297Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox"));
		waitResponse();

		Assertions.assertNotEquals(
			Color.fromString(jq("@listitem.z-listitem-selected").find("@listcell").css("backgroundColor")),
			Color.fromString(jq("@listitem:eq(0)").find("@listcell").css("backgroundColor"))
		);
	}
}
