/* B102_ZK_5954Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 13 20:55:53 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5954Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        click(jq("@button"));
        waitResponse();
        click(jq("@button"));
        waitResponse();
        assertNoAnyError();
        Assertions.assertEquals("abc", jq("$container").text().replaceAll("[\\s-]", ""));
    }
}
