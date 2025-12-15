/* B103_ZK_6006Test.java

        Purpose:

        Description:

        History:
                Thu Oct 30 10:39:27 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

        String originalTimeStr = input.val();
        LocalTime originalTime = parseTime(originalTimeStr);

        click(btn);
        // press the up arrow key twice to select a new time
        getActions().sendKeys(Keys.ARROW_UP, Keys.ARROW_UP, Keys.ENTER).perform();

        String newTimeStr = input.val();
        LocalTime newTime = parseTime(newTimeStr);

        JQuery showValBtn = jq(".z-button");
        click(showValBtn);
        waitResponse();

        JQuery msg = jq(".z-label").last();
        String msgTimeStr = msg.text();
        msgTimeStr = msgTimeStr.replaceAll(".*Time\\s*:\\s*(\\d{2}:\\d{2}).*", "$1");
        LocalTime msgTime = parseTime(msgTimeStr);

        Assertions.assertEquals(newTime, msgTime);
        Assertions.assertNotEquals(originalTime, msgTime);
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr.contains("AM") || timeStr.contains("PM")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ss a");
            return LocalTime.parse(timeStr, formatter);
        }
        else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");
                return LocalTime.parse(timeStr, formatter);
            } catch (Exception e) {
                // Fall back to format without seconds
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
                return LocalTime.parse(timeStr, formatter);
            }
        }
    }
}
