/* B103_ZK_6006Test.java

        Purpose:

        Description:

        History:
                Thu Oct 30 10:39:27 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_6006Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery timepicker = jq(".z-timepicker").eq(0);
        JQuery input = timepicker.find(".z-timepicker-input");
        JQuery btn = timepicker.find(".z-timepicker-button");

        String originalTime = removeSeconds(input.val());

        click(btn);
        // press the up arrow key twice to select a new time
        getActions().sendKeys(Keys.ARROW_UP, Keys.ARROW_UP, Keys.ENTER).perform();

        String newTime = removeSeconds(input.val());

        JQuery showValBtn = jq(".z-button");
        click(showValBtn);
        waitResponse();

        JQuery msg = jq(".z-label").last();
        String timeInMsg = msg.text();
        timeInMsg = timeInMsg.replaceAll(".*Time\\s*:\\s*(\\d{2}:\\d{2}).*", "$1");

        Assertions.assertEquals(newTime, timeInMsg);
        Assertions.assertNotEquals(originalTime, timeInMsg);
    }

    private String removeSeconds(String time) {
        return time.replaceAll(":\\d{2}$", "");
    }
}
