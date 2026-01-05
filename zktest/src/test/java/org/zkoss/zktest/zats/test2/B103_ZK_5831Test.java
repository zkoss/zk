/* B103_ZK_5831Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 31 12:38:57 CST 2025, Created by josephlo

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5831Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery primaryCol = jq(".z-column").first();
        WebElement primaryElement = toElement(primaryCol);
        assertTrue(primaryElement.getSize().getWidth() > 400);
    }
}