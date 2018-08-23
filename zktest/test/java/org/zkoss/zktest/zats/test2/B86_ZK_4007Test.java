/* B86_ZK_4007Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 23 10:37:56 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.openqa.selenium.By;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class B86_ZK_4007Test extends WebDriverTestCase {
    @Test
    public void test() {
        driver = connect();
        Actions act = new Actions(driver);
        WebElement win = toElement(jq("$window"));
        act.dragAndDropBy(win.findElement(By.className("z-window-header-move")),10,0).build().perform();
        WebElement frame = driver.findElement(By.id("z_ddstkup"));
        act.moveToElement(frame,200,199);
        act.clickAndHold().moveByOffset(0, 300).release();
        act.build().perform();
        assertNotEquals(100,win.getLocation().getY());
    }
}
