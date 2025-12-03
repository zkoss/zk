/* B103_ZK_5731Test.java

        Purpose:

        Description:

        History:
                Wed Oct 29 11:05:00 CST 2025, Created by peaker

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5731Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        waitResponse();
        click(jq("$btn"));
        waitResponse();
        assertNoAnyError();
    }
}
