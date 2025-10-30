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
import org.zkoss.zk.au.http.TimeZoneDataBaseVersionChecker;

public class F102_ZK_5640_3Test extends F102_ZK_5640Test {

    @BeforeAll
    public static void beforeAll() throws Exception {
        Library.setProperty("org.zkoss.zk.moment.timezone.path", "/test2/data/2017a.json");
        beforeAll0();
    }

    @AfterAll
    public static void afterAll0() {
        Library.setProperty("org.zkoss.zk.moment.timezone.path", null);
    }

    @Test
    public void test() {
        Assertions.assertEquals("2017a", TimeZoneDataBaseVersionChecker.getClientTimeZoneDataBaseVersion());
    }
}
