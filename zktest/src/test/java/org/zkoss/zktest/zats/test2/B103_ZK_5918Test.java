/* B103_ZK_5918Test.java

        Purpose:

        Description:

        History:
                Tue Dec 23 14:05:24 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5918Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long initialScrollY = (Long) js.executeScript("return window.pageYOffset;");
        Actions actions = getActions();

        String moldIsActive = "document.activeElement === jq('.z-checkbox-switch .z-checkbox-mold')[0]";
        for (int i = 0; i < 6 && !"true".equals(getEval(moldIsActive)); i++)
            actions.sendKeys(Keys.TAB).perform();
        Assertions.assertEquals("true", getEval(moldIsActive));
        actions.sendKeys(Keys.SPACE).perform();
        waitResponse();
        Long currentScrollY = (Long) js.executeScript("return window.pageYOffset;");

        Assertions.assertEquals(initialScrollY, currentScrollY);
    }
}
