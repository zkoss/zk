/* B104_ZK_5767Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jan 21 17:23:21 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_5767Test extends WebDriverTestCase {
    @Test
    public void test(){
        connect();

        WebElement minHeaderContent = driver.findElement(By.cssSelector("th:nth-child(4) .z-column-content"));
        assertNotNull(minHeaderContent);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Number scrollWidth = (Number) js.executeScript("return arguments[0].scrollWidth", minHeaderContent);
        Number clientWidth = (Number) js.executeScript("return arguments[0].clientWidth", minHeaderContent);

        assertNotNull(scrollWidth);
        assertNotNull(clientWidth);

        long sw = scrollWidth.longValue();
        long cw = clientWidth.longValue();

        assertTrue(sw <= cw);
    }
}
