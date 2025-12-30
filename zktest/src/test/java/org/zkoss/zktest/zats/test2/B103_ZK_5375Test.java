/* B103_ZK_5375Test.java

        Purpose:

        Description:

        History:
                Tue Dec 30 16:00:50 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5375Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery centerRegion = jq(".z-center");
        int initialHeight = centerRegion.outerHeight();
        int initialWidth = centerRegion.outerWidth();

        click(jq("@button:eq(0)"));
        click(jq("@button:eq(1)"));
        waitResponse();

        Assertions.assertNotEquals(initialHeight, centerRegion.outerHeight());
        Assertions.assertNotEquals(initialWidth, centerRegion.outerWidth());
    }
}
