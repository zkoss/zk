/* F102_ZK_5946Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 08 17:53:52 CST 202", "reated by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogEntry;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F102_ZK_5946Test extends WebDriverTestCase {

    private static final String[] oldTimezoneMappings = new String[] {
            // Java old mappings
            "ACT", "AET", "AGT", "ART", "AST",
            "BET", "BST", "CAT", "CNT", "CST",
            "CTT", "EAT", "ECT", "IET", "IST",
            "JST", "MIT", "NET", "NST", "PLT",
            "PNT", "PRT", "PST", "SST", "VST",
            // Java SystemV mappings
            "SystemV/AST4", "SystemV/AST4ADT",
            "SystemV/EST5", "SystemV/EST5EDT",
            "SystemV/CST6", "SystemV/CST6CDT",
            "SystemV/MST7", "SystemV/MST7MDT",
            "SystemV/PST8", "SystemV/PST8PDT",
            "SystemV/YST9", "SystemV/YST9YDT",
            "SystemV/HST10"
    };
    private static final String[] nonWholeHourGMTs = new String[] {
            "GMT+00:15", "GMT+05:30", "GMT+6:45", "GMT+11:59",
            "GMT-01:15", "GMT-08:30", "GMT-11:45", "GMT-13:59",
    };

    private final List<String> errorList = new ArrayList<>();

    @Test
    public void test() {
        connect();
        for (String timezone : oldTimezoneMappings)
            test0(timezone);
        for (String timezone : nonWholeHourGMTs)
            test0(timezone);
        Assertions.assertEquals(0, errorList.size());
    }

    // set java timezone and reload the page to apply timezone change to client side
    // and save the error timezone into errorList
    private void test0(String timezone) {
        System.setProperty("user.timezone", timezone);
        TimeZone.setDefault(null); // trigger java reload new timezone from "user.timezone" system property
        driver.navigate().refresh(); // reload the page to apply new timezone to client side
        waitResponse();
        click(jq("$setDateBtn"));
        waitResponse();
        // if moment.js has error or the set date not equals to "2024/01/01 00:00:00", then the error exists
        if (hasMomentJsTimeZoneError() || !"2024/01/01 00:00:00".equals(jq("@datebox input").val()))
            errorList.add(timezone);
    }

    private boolean hasMomentJsTimeZoneError() {
        LogEntry log = driver.manage()
                .logs()
                .get("browser")
                .getAll()
                .stream()
                .filter(
                        (entry) -> entry.getLevel().intValue() >= Level.SEVERE.intValue()
                ).filter(
                        (entry) -> entry.getMessage().contains("Moment Timezone has no data for")
                )
                .findFirst()
                .orElse(null);
        return log != null; 
    }
}
