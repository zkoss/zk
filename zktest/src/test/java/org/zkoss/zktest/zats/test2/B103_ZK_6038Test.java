/* B103_ZK_6038Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Jan 12 18:02:29 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.Color;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_6038Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery coachmarkMask = jq(".z-coachmark-mask");

        Color maskColor = Color.fromString(coachmarkMask.css("background-color"));
        Color transparent = Color.fromString("rgba(0, 0, 0, 0)");

        Assertions.assertNotEquals(transparent, maskColor);
    }
}
