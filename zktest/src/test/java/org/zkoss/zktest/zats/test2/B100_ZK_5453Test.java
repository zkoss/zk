/* B100_ZK_5453Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 25 16:45:04 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B100_ZK_5453Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery chosenbox = jq("@chosenbox");
        click(chosenbox);
        waitResponse();
        JQuery input = chosenbox.find("input");
        String content = "'\"><img src=foo onerror=alert(/XSS-/+location)>";
        sendKeys(input, content, Keys.ENTER);
        assertFalse(showAlert());
        assertEquals(content, jq(".z-chosenbox-item-content").text());
    }

    public boolean showAlert() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
