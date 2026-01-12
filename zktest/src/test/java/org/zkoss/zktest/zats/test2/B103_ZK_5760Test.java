/* B103_ZK_5760Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Jan 09 17:15:41 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5760Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery column = jq("@column").first();
        WebElement columnEl = toElement(column);
        int width = column.outerWidth();
        Actions actions = new Actions(driver);
        int rightEdge = (width / 2) - 1;

        actions.moveToElement(columnEl, rightEdge, 0)
                .pause(Duration.ofMillis(500))
                .clickAndHold()
                .moveByOffset(-20, 0)
                .pause(Duration.ofMillis(500))
                .release()
                .perform();

        waitResponse();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement logTextArea = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("zk_log")));
        String logText = logTextArea.getAttribute("value");
        String[] lines = logText.trim().split("\n");
        String lastLine = lines[lines.length - 1].trim();
        int reportedWidth = Integer.parseInt(lastLine.replaceAll("[^0-9]", ""));
        int actualWidth = jq("@column").first().outerWidth();
        int diff = Math.abs(reportedWidth - actualWidth);
        assertTrue(diff <= 1);
    }
}