/* B103_ZK_5683Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 12 09:58:27 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5683Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();
        sleep(1000);
        click(jq("@listitem:eq(0)"));
        waitResponse();

        getActions().keyDown(Keys.SHIFT)
                .click(toElement(jq("@listitem:eq(2)")))
                .keyUp(Keys.SHIFT)
                .perform();
        waitResponse();

        String selectionLabel = jq("@label:last").text();
        Assertions.assertTrue(selectionLabel.contains("Auto"));
        Assertions.assertTrue(selectionLabel.contains("Telephone"));
        Assertions.assertTrue(selectionLabel.contains("Zeppelin"));

        assertNoAnyError();
    }
}
