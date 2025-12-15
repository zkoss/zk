/* B103_ZK_5837Test.java

        Purpose:
                
        Description:
                
        History:
                Sat Dec 06 17:40:15 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5837Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        String dateboxIconColor = jq(".z-datebox:not(.z-datebox-disabled) .z-datebox-icon").css("color");
        String bandboxIconColor = jq(".z-bandbox:not(.z-bandbox-disabled) .z-bandbox-icon").css("color");
        String cascaderIconColor = jq(".z-cascader:not(.z-cascader-disabled) .z-cascader-icon").css("color");
        String timeboxIconColor = jq(".z-timebox:not(.z-timebox-disabled) .z-timebox-icon").css("color");
        String spinnerIconColor = jq(".z-spinner:not(.z-spinner-disabled) .z-spinner-icon").css("color");
        String doublespinnerIconColor = jq(".z-doublespinner:not(.z-doublespinner-disabled) .z-doublespinner-icon").css("color");

        Assertions.assertEquals(dateboxIconColor, bandboxIconColor);
        Assertions.assertEquals(dateboxIconColor, cascaderIconColor);
        Assertions.assertEquals(dateboxIconColor, timeboxIconColor);
        Assertions.assertEquals(dateboxIconColor, spinnerIconColor);
        Assertions.assertEquals(dateboxIconColor, doublespinnerIconColor);

        String dateboxDisabledIconColor = jq(".z-datebox-disabled .z-datebox-icon").css("color");
        String bandboxDisabledIconColor = jq(".z-bandbox-disabled .z-bandbox-icon").css("color");
        String cascaderDisabledIconColor = jq(".z-cascader-disabled .z-cascader-icon").css("color");
        String timeboxDisabledIconColor = jq(".z-timebox-disabled .z-timebox-icon").css("color");
        String spinnerDisabledIconColor = jq(".z-spinner-disabled .z-spinner-icon").css("color");
        String doublespinnerDisabledIconColor = jq(".z-doublespinner-disabled .z-doublespinner-icon").css("color");

        Assertions.assertEquals(dateboxDisabledIconColor, bandboxDisabledIconColor);
        Assertions.assertEquals(dateboxDisabledIconColor, cascaderDisabledIconColor);
        Assertions.assertEquals(dateboxDisabledIconColor, timeboxDisabledIconColor);
        Assertions.assertEquals(dateboxDisabledIconColor, spinnerDisabledIconColor);
        Assertions.assertEquals(dateboxDisabledIconColor, doublespinnerDisabledIconColor);

        assertNoAnyError();
    }
}
