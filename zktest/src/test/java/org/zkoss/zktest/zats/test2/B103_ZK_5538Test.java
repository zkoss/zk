/* B103_ZK_5538Test.java

        Purpose:

        Description:

        History:
                Fri Dec 26 18:11:59 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5538Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        WebElement draggableDiv = driver.findElement(jq(".z-div"));
        Actions actions = new Actions(driver);
        actions.dragAndDropBy(draggableDiv, 0, 300).perform();

        waitResponse();

        JQuery radioBtn = jq(".z-radio").eq(0).find("input");
        boolean checked = radioBtn.is(":checked");

        Assertions.assertTrue(checked);
    }
}
