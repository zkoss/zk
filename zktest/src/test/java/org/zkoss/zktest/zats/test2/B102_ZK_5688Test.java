/* B102_5688Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 24 16:17:13 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5688Test extends WebDriverTestCase {

/**
 * @author cherrylee
 */
    @Override
    protected ChromeOptions getWebDriverOptions() {
        return super.getWebDriverOptions()
                .addArguments("--lang=de-DE")
                .setExperimentalOption("prefs", Map.of(
                        "intl.accept_languages", "de-DE"
                ));
    }

    @Test
    public void test() throws FileNotFoundException {
        connect();
        // 1. no negative, no zero
        click(jq("button:contains(\"set value to 0\")"));
        waitResponse();
        assertTrue(getErrorMessage().contains("Nur positive Zahlen sind erlaubt"));

        // 2. only blank allowed
        driver.findElement(By.className("z-errorbox-close")).click();
        click(jq("button:contains(\"set value to 3\")"));
        waitResponse();
        assertTrue(getErrorMessage().contains("Nur leer"));

        // 3. only today allowed
        driver.findElement(By.className("z-errorbox-close")).click();
        eval("jq('.z-errorbox-close').click()");
        click(jq("button:contains(\"set to Tomorrow\")"));
        waitResponse();
        assertTrue(getErrorMessage().contains("Nur das heutige Datum ist erlaubt"));

        // 4. today not allowed
        driver.findElement(By.className("z-errorbox-close")).click();
        click(jq("button:contains(\"set to today\")"));
        waitResponse();
        assertTrue(getErrorMessage().contains("Heute ist nicht erlaubt"));

        // 5. upload file with wrong format
        dropUploadFile(jq("@dropupload"), Paths.get("src/main/webapp/test2/img/sun.jpg"));
        waitResponse();
        List<WebElement> labels = driver.findElements(By.cssSelector(".z-window-modal .z-window-content .z-label"));
        String errorMsg = labels.get(1).getText();
        System.out.println(errorMsg);
        assertTrue(errorMsg.contains("Die Anfrage wurde abgelehnt, weil der Dateityp"));
    }

    private String getErrorMessage() {
        WebElement errBox = driver.findElement(By.className("z-errorbox"));
        return errBox.getText();
    }
}


