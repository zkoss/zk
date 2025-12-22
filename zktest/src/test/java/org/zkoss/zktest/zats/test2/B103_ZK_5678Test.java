/* B103_ZK_5678.java

        Purpose:

        Description:

        History:
                Thu Dec 04 12:39:16 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5678Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery scrollBar = jq(".z-frozen-inner").eq(0);
        int initialScrollPos = scrollBar.scrollLeft();

        JQuery input = jq(".z-textbox").eq(0);
        click(input);

        Actions actions = getActions();
        for (int i = 0; i < 5; i++) {
            actions.sendKeys(Keys.TAB).perform();
        }
        actions.perform();
        waitResponse();

        int finalScrollPos = scrollBar.scrollLeft();

        Assertions.assertNotEquals(initialScrollPos, finalScrollPos);
    }
}
