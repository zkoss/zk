/* B104_ZK_5608Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 22 11:40:55 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_5608Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ESCAPE).perform();
        waitResponse();
        assertTrue(getZKLog().contains("onCancel"));

        closeZKLog();
        waitResponse();

        actions.sendKeys(Keys.ENTER).perform();
        waitResponse();
        assertTrue(getZKLog().contains("onOK"));
    }
}
