/* B103_ZK_5875Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Dec 15 17:43:36 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F103_ZK_5875Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery chosenInput = jq(".z-chosenbox-input");
        JQuery dropdownOptions = jq(".z-chosenbox-option");

        sendKeys(chosenInput, "ZK Co");
        waitResponse();
        Assertions.assertEquals(2, dropdownOptions.filter(":visible").length());
        Assertions.assertTrue(isOptionVisible("ZK Contributor"));
        Assertions.assertTrue(isOptionVisible("ZK Community"));

        click(jq("body"));
        click(chosenInput);
        sendKeys(chosenInput, "zk");
        waitResponse();
        int totalOptions = dropdownOptions.length();
        Assertions.assertEquals(totalOptions, dropdownOptions.filter(":visible").length());

        click(jq("body"));
        click(chosenInput);
        sendKeys(chosenInput, "ZK Developer");
        waitResponse();
        Assertions.assertEquals(1, dropdownOptions.filter(":visible").length());
        Assertions.assertTrue(isOptionVisible("ZK Developer"));

        click(jq("body"));
        click(chosenInput);
        sendKeys(chosenInput, "test");
        waitResponse();
        Assertions.assertEquals(2, dropdownOptions.filter(":visible").length());
        Assertions.assertTrue(isOptionVisible("ZKtest"));
        Assertions.assertTrue(isOptionVisible("ZK Test Case"));

        assertNoAnyError();
    }

    @Test
    public void testOverride() {
        connect("/test2/F103-ZK-5875Override.zul");

        JQuery chosenInput = jq(".z-chosenbox-input");
        JQuery dropdownOptions = jq(".z-chosenbox-option");

        sendKeys(chosenInput, "oss");
        waitResponse();
        Assertions.assertEquals(0, dropdownOptions.filter(":visible").length());
        assertNoAnyError();
    }

    private boolean isOptionVisible(String optionText) {
        JQuery visibleOptions = jq(".z-chosenbox-option").filter(":visible");
        for (int i = 0; i < visibleOptions.length(); i++) {
            if (visibleOptions.eq(i).text().contains(optionText)) {
                return true;
            }
        }
        return false;
    }
}
