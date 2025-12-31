/* B103_ZK_5870Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Dec 29 12:31:21 CST 2025, Created by josephlo

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5870Test extends WebDriverTestCase {
    @BeforeAll
    public static void setTZpath() {
        Library.setProperty("org.zkoss.zkmax.au.websocket.WebSocketEndPoint.urlPattern", "/myappws/");
        Library.setProperty("org.zkoss.zkmax.au.websocket.WebSocketEndPoint.pingIntervalSecond", "20");
    }

    @AfterAll
    public static void cleanUp() {
        Library.setProperty("org.zkoss.zkmax.au.websocket.WebSocketEndPoint.urlPattern", null);
    }

    @Test
    public void test() throws IOException {
        File tempFile = File.createTempFile("test_5870", ".txt");
        try {
            connect();
            click(jq("@button"));
            waitResponse();
            WebElement fileInput = driver.findElement(By.cssSelector("input[type='file']"));
            fileInput.sendKeys(tempFile.getAbsolutePath());
            assertFalse(jq(".z-messagebox-error").exists());
        } catch (Exception e) {
            Assertions.fail("Should not have any exception");
        } finally {
            tempFile.delete();
        }
    }
}