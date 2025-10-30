/* F102_ZK_5640_1Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 07 16:18:50 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class F102_ZK_5640_1Test extends F102_ZK_5640Test {

    @BeforeAll
    public static void beforeAll() throws Exception {
        createTempTZDBFileAndApplyToLibraryProperty(getJavaTZDBVersion());
        beforeAll0();
    }

    @AfterAll
    public static void afterAll() throws Exception {
        afterAll0();
    }

    @Test
    public void test() { // test same version
        StringBuilder sb = new StringBuilder();
        for (String log : _handler.getLogs())
            sb.append(log);
        // if 2 version are the same, shouldn't log warning
        Assertions.assertEquals("", sb.toString());
    }
}
