/* B104_ZK_5562Test.java

    Purpose:
        
    Description:
        
    History:
        Wed Feb 04 10:13:47 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5562Test extends WebDriverTestCase {
    @Override
    public boolean isHeadless() {
        return false;
    }
    @Test
    public void test() {
        connect();
        JQuery editMenu = jq("@menu[label=\"Edit\"]");
        click(editMenu);
        waitResponse();
        JQuery undoItem = jq("@menuitem[label=\"Undo\"]");
        click(undoItem);
        waitResponse();
        boolean isFocused = jq("@menu[label=\"Edit\"]").find("a").is(":focus");
        assertFalse(isFocused);
        Actions action = getActions();
        WebElement editMenuA = toElement(jq("@menu[label=\"Edit\"]").find("a"));
        editMenuA.sendKeys(Keys.ENTER);
        waitResponse();
        action.sendKeys(Keys.ARROW_DOWN).build().perform();
        waitResponse();
        action.sendKeys(Keys.ENTER).build().perform();
        waitResponse();
        isFocused = jq("@menu[label=\"Edit\"]").find("a").is(":focus");
        assertTrue(isFocused);
    }
}
