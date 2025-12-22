/* B103_ZK_5838Test.java

        Purpose:
                
        Description:
                
        History:
                Sat Dec 06 16:59:05 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5838Test extends WebDriverTestCase {
    @Test
    public void test() { connect();
        waitResponse();

        String dateboxInputWidth = jq(".z-datebox-input").css("width");
        String dateboxInputHeight = jq(".z-datebox-input").css("height");
        String dateboxButtonWidth = jq(".z-datebox-button").css("width");
        String dateboxButtonHeight = jq(".z-datebox-button").css("height");
        String dateboxIconWidth = jq(".z-datebox-icon").css("width");
        String dateboxIconHeight = jq(".z-datebox-icon").css("height");

        String timepickerInputWidth = jq(".z-timepicker-input").css("width");
        String timepickerInputHeight = jq(".z-timepicker-input").css("height");
        String timepickerButtonWidth = jq(".z-timepicker-button").css("width");
        String timepickerButtonHeight = jq(".z-timepicker-button").css("height");
        String timepickerIconWidth = jq(".z-timepicker-icon").css("width");
        String timepickerIconHeight = jq(".z-timepicker-icon").css("height");

        Assertions.assertEquals(dateboxInputWidth, timepickerInputWidth);
        Assertions.assertEquals(dateboxInputHeight, timepickerInputHeight);
        Assertions.assertEquals(dateboxButtonWidth, timepickerButtonWidth);
        Assertions.assertEquals(dateboxButtonHeight, timepickerButtonHeight);
        Assertions.assertEquals(dateboxIconWidth, timepickerIconWidth);
        Assertions.assertEquals(dateboxIconHeight, timepickerIconHeight);

        assertNoAnyError();
    }
}
