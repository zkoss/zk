/* B103_ZK_5227.java

        Purpose:

        Description:

        History:
                Fri Nov 07 16:36:59 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5227Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery combobox = jq(".z-combobox-input").eq(0);
        JQuery comboboxWithAutodrop = jq(".z-combobox-input").eq(1);
        JQuery dropdown = jq(".z-combobox-popup.z-combobox-open");

        // check if the up arrow key opens the dropdown
        click(comboboxWithAutodrop);
        getActions().sendKeys(Keys.ARROW_UP).perform();
        waitResponse();
        Assertions.assertTrue(dropdown.exists());

        click(combobox);
        waitResponse();

        // check if the down arrow key opens the dropdown
        click(comboboxWithAutodrop);
        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        waitResponse();
        Assertions.assertTrue(dropdown.exists());
    }
}
