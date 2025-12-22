/* B103_ZK_5684Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 12 15:39:16 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5684Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        String firstRowText = jq("@row:eq(0)").text();
        String secondRowText = jq("@row:eq(1)").text();
        String thirdRowText = jq("@row:eq(2)").text();

        click(jq(".z-button").eq(0));
        waitResponse();

        Assertions.assertEquals(firstRowText, jq("@row:eq(0)").text(), "First row should remain unchanged");
        Assertions.assertNotEquals(secondRowText, jq("@row:eq(1)").text(), "Second row should be updated");
        Assertions.assertEquals(thirdRowText, jq("@row:eq(2)").text(), "Third row should remain unchanged");

        assertNoAnyError();
    }
}
