package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5864Test extends WebDriverTestCase {
    @Test
    public void test() throws InterruptedException {
        connect();

        eval("zk.load('zk.debug')");
        waitResponse();

        assertEquals("true", getEval("typeof zDebug !== 'undefined'"));

        eval("zDebug.dumpWidgetTree4Zul(zk.Desktop._dt)");
        waitResponse();

        WebElement debuggerDiv = driver.findElement(By.id("zk_debugger"));
        assertNotNull(debuggerDiv);
        assertTrue(debuggerDiv.isDisplayed());

        String display = debuggerDiv.getCssValue("display");
        assertNotEquals("none", display);
    }
}