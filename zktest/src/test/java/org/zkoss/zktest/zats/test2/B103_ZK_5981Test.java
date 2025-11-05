/* B103_ZK_5981.java

        Purpose:

        Description:

        History:
                Tue Nov 04 16:13:17 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5981Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery dataHandlerBtn = jq("@button").eq(0);
        click(dataHandlerBtn);
        waitResponse();
        assertNoJSError();
    }
}
