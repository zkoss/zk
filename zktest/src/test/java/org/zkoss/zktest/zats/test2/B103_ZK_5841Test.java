/* B103_ZK_5841Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 17 15:46:12 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5841Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        WebElement scriptWithMyId = driver.findElement(By.id("myid"));
        assertNotNull(String.valueOf(scriptWithMyId));
        assertEquals("script", scriptWithMyId.getTagName().toLowerCase());

        WebElement scriptWithAnotherId = driver.findElement(By.id("anotherid"));
        assertNotNull(String.valueOf(scriptWithAnotherId));
        assertEquals("script", scriptWithAnotherId.getTagName().toLowerCase());

        assertNoAnyError();
    }
}
