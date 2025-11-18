/* B103_ZK_6025.java

        Purpose:

        Description:

        History:
                Mon Nov 17 12:08:37 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_6025Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        click(jq(".z-slider")); // default center
        waitResponse();

        JQuery sliderButton = jq(".z-slider-button");
        String titleAt50 = sliderButton.attr("title");
        assertEquals("50", titleAt50);

        assertNoJSError();
    }
}
