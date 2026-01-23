/* B103_ZK_5919Test.java

        Purpose:

        Description:

        History:
                Fri Jan 23 16:05:24 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5919Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        Actions actions = getActions();
        actions.sendKeys(Keys.TAB).perform();
        waitResponse();
        actions.sendKeys(Keys.TAB).perform();
        waitResponse();
        actions.sendKeys(Keys.ENTER).perform();
        waitResponse();

        assertTrue(jq(".z-listheader-checkable").hasClass("z-listheader-checked"));
        int total = jq(".z-listitem").length();
        assertTrue(total == jq(".z-listitem-selected").length());

        actions.sendKeys(Keys.ENTER).perform();
        waitResponse();
        actions.sendKeys(Keys.TAB).perform();
        waitResponse();
        actions.sendKeys(Keys.ENTER).perform();
        waitResponse();

        assertTrue(jq(".z-listitem").first().hasClass("z-listitem-selected"));
    }
}
