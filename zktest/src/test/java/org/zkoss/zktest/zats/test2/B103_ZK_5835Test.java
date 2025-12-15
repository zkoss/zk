/* B103_ZK_5835Test.java

        Purpose:

        Description:

        History:
                Sun Dec 07 18:54:45 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5835Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery comboButton = jq(".z-combobutton-content");
        click(comboButton);
        waitResponse();

        JQuery menuItem = jq(".z-menuitem");
        click(menuItem);
        waitResponse();

        JQuery notification = jq(".z-notification");
        Assertions.assertTrue(notification.isVisible());
    }
}
