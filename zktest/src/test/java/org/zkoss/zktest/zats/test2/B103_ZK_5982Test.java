/* B103_ZK_5982.java

        Purpose:

        Description:

        History:
                Wed Oct 29 17:46:41 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5982Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery addButton = jq("@button");
        click(addButton);
        waitResponse();
        assertNoJSError();
    }
}
