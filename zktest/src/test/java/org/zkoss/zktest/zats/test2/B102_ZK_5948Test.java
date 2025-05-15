/* B102_ZK_5948Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 13 15:26:49 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5948Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();
        click(jq("@button"));
        waitResponse();
        Assertions.assertEquals("bbaaccdd", jq("$container").text().replaceAll("[\\s-]", ""));
    }
}
