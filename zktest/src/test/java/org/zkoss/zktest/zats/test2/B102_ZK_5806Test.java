/* B102_ZK_5806Test.java

        Purpose:
                
        Description:
                
        History:
                Fri May 02 13:14:58 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5806Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        JQuery popup = jq("@popup");
        Assertions.assertFalse(popup.isVisible());
        click(jq("@button"));
        waitResponse();
        Assertions.assertTrue(popup.isVisible());
    }
}
