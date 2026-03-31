/* B104_ZK_6047Test.java

        Purpose:

        Description:

        History:
                Tue Feb 5 17:56:35 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_6047Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery btn = jq("@button");
        click(btn);
        waitResponse();
        assertTrue(jq(".z-notification").exists());

        ((JavascriptExecutor) driver).executeScript("window.scroll(0, 50)");
        click(btn);
        waitResponse();
        assertTrue(jq(".z-notification").exists());

        // Wait for previous notifications to be dismissed, then verify the negative case.
        sleep(2500);
        assertFalse(jq(".z-notification").exists());

        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Boolean isWindowOutOfView = (Boolean) executor.executeScript(
            "var win = document.querySelector('.z-window');"
                + "if (!win) return false;"
                + "var rect = win.getBoundingClientRect();"
                + "return rect.bottom <= 0 || rect.top >= window.innerHeight;");
        assertTrue(Boolean.TRUE.equals(isWindowOutOfView));

        // Trigger click through JS to avoid WebDriver auto-scroll behavior.
        executor.executeScript("document.querySelector('.z-button').click();");
        waitResponse();
        assertFalse(jq(".z-notification").exists());
    }
}
