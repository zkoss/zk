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

public class B102_ZK_5955Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        click(jq("@button:eq(0)"));
        waitResponse();
        click(jq("@button:eq(1)"));
        waitResponse();
        assertNoAnyError();
        Assertions.assertEquals("bca", jq("$container").text().replaceAll("[\\s-]", ""));
    }
}
