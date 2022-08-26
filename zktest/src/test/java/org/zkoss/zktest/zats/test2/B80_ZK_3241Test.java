/* B80_ZK_3241Test.java
	Purpose:

	Description:

	History:
Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Created by jameschu
 */
public class B80_ZK_3241Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
		click(jq("@button").first());
		waitResponse();
		assertEquals(0, jq(".z-errorbox:visible").length());
		click(jq("@button").last());
		waitResponse();
		assertEquals(0, jq(".z-errorbox:visible").length());
    }
}
