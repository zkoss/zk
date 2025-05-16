/* F102_ZK_5640_3Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 07 16:21:40 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zk.ui.http.Wpds;

public class F102_ZK_5640_3Test extends WebDriverTestCase {

    @BeforeAll
    public static void beforeAll() {
        Library.setProperty("org.zkoss.zk.moment.timezone.path", "/test2/data/2017a.json");
    }

    @AfterAll
    public static void afterAll() {
        Library.setProperty("org.zkoss.zk.moment.timezone.path", null);
    }

    @Test
    public void test() {
        connect("/test2/F102-ZK-5640.zul");
        waitResponse();
        Assertions.assertEquals("2017a", Wpds.getClientTimeZoneDataBaseVersion());
    }
}
