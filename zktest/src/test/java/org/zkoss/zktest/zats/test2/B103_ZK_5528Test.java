/* B103_ZK_5528Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Dec 22 11:39:28 CST 2025, Created by josephlo

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5528Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery firstTextbox = jq("@textbox").eq(0);
        click(firstTextbox);

        Actions actions = new Actions(driver);

        actions.sendKeys(Keys.TAB).perform();
        actions.sendKeys(Keys.TAB).perform();

        WebElement focusedElement = driver.switchTo().activeElement();
        String focusedClass = focusedElement.getAttribute("class");
        assertTrue(focusedClass.contains("z-toolbarbutton"));

        while (driver.switchTo().activeElement().getAttribute("class").contains("z-toolbarbutton")) {
            actions.sendKeys(Keys.TAB).perform();
        }

        focusedElement = driver.switchTo().activeElement();
        focusedClass = focusedElement.getAttribute("class");

        assertTrue(focusedClass.contains("z-textbox"));
    }
}