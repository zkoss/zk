/* B102_ZK_5955Test.java

        Purpose:

        Description:

        History:
                Wed May 14 00:30:52 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5090Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        String windowPadding = jq(".z-window").css("padding");
        Assertions.assertEquals("0px", windowPadding);

        String contentPadding = jq(".z-window-content").css("padding");
        Assertions.assertEquals("0px", contentPadding);

        assertNoAnyError();
    }
}
