/* F102_ZK_5888Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Apr 28 17:36:54 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class F102_ZK_5888Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        getActions().sendKeys(Keys.TAB, Keys.ARROW_DOWN, Keys.ARROW_UP).perform();
        Assertions.assertEquals(jq(".z-treecol-checkable").attr("id"), getEval("document.activeElement.id"));
    }
}
