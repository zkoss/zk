/* B102_ZK_5741Test.java

        Purpose:
                
        Description:
                
        History:
                Fri May 02 12:53:21 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5741Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        JQuery button = jq("@button"),
                icon = jq("@a > i"),
                label = jq("@a @label");
        click(button);
        waitResponse();
        Assertions.assertFalse(icon.prev().exists());
        Assertions.assertEquals(label.attr("id"), icon.next().attr("id"));
    }
}
