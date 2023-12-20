/* F100_ZK_5512Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Dec 18 16:58:13 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F100_ZK_5512Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@textbox"));
		waitResponse();
		getActions().sendKeys(Keys.SPACE).perform();
		waitResponse();
		assertEquals(getZKLog(), "Key pressed: 32");
	}
}
