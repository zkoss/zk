/* B102_ZK_5887Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 14 11:24:56 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5887Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq("@button:eq(2)"));
        waitResponse();
        click(jq("@button:eq(1)"));
        waitResponse();
        click(jq("@button:eq(0)"));
        waitResponse();
        assertNoAnyError();
    }
}
