/* B103_ZK_5207Test.java

        Purpose:

        Description:

        History:
                Wed Nov 05 16:03:23 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5207Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery inputField = jq(".z-combobox-input");
        String originalInput = inputField.val();

        JQuery clearBtn = jq("@button");
        click(clearBtn);
        waitResponse();
        String clearedInput = inputField.val();

        Assertions.assertEquals("", clearedInput);
        Assertions.assertNotEquals(originalInput, clearedInput);
    }
}
