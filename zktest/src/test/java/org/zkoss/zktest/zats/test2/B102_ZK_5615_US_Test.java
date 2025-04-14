/* B102_ZK_5861Test.java

 	Purpose:

 	Description:

 	History:
 		Mon Apr 14 11:31:22 CST 2025, Created by cherrylee

 Copyright (C) 2024 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author cherrylee
 */
public class B102_ZK_5615_US_Test extends WebDriverTestCase {

    private final String locale = "en-US";

    @Override
    protected ChromeOptions getWebDriverOptions() {
        return super.getWebDriverOptions()
                .addArguments("--lang=" + locale)
                .setExperimentalOption("prefs", Map.of(
                        "intl.accept_languages", locale
                ));
    }

    @Test
    public void testDateboxInUSLocale() {

        connect("/test2/B102-ZK-5615.zul");

        JQuery datebox = jq(".z-datebox");

        click(datebox.find(".z-datebox-button"));
        waitResponse();

        JQuery leftArrow = jq(".z-calendar-left");
        for (int i = 0; i < 3; i++) {
            click(leftArrow);
            waitResponse();
        }

        JQuery firstAvailableDate = jq(".z-calendar-cell:visible")
                .filter(":not(.z-calendar-outside)")
                .eq(17);
        String selectedDateText = firstAvailableDate.text();
        click(firstAvailableDate);
        waitResponse();

        String dateboxValue = getEval("zk.Widget.$('.z-datebox').getInputNode().value");
        assertThat("Datebox input date should be: " + selectedDateText + " (Locale: " + locale + ")", dateboxValue, containsString(selectedDateText));

    }

}
