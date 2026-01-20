/* B104_ZK_6039Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 20 15:42:02 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.fail;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_6039Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        LogEntries logEntries = getWebDriver().manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            if (entry.getLevel().equals(Level.SEVERE)) {
                fail("Found JavaScript error in browser console: " + entry.getMessage());
            }
        }
    }
}