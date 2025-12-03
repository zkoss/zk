/* B103_ZK_5988Test.java

        Purpose:

        Description:

        History:
                Tue Nov 25 17:37:48 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@ForkJVMTestOnly
public class B103_ZK_5988Test extends WebDriverTestCase {
    @RegisterExtension
    public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

    @Override
    protected ChromeOptions getWebDriverOptions() {
        return super.getWebDriverOptions()
                .setExperimentalOption("mobileEmulation", Collections
                        .singletonMap("deviceName", "iPad"));
    }

    @Test
    public void test() {
        connect();

        // Test the popup position, which should be at the mouse pointer
        JQuery popupBtn = jq(".z-a").eq(0);
        click(popupBtn);
        waitResponse();

        JQuery popupElement = jq(".z-popup");
        String style = popupElement.attr("style");
        Assertions.assertFalse(style.contains("top: 0px"));
        Assertions.assertFalse(style.contains("left: 0px"));

        // Open B103-ZK-5988.zul to test the scrollable issue of the listbox manually
    }
}
