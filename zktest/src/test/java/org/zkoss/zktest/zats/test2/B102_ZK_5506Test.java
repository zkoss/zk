/* B102_ZK_5506Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 24 18:16:56 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class B102_ZK_5506Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertTrue(isFocusOnTree());
        Assertions.assertFalse(isFocusOnBody());
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertFalse(isFocusOnTree());
        Assertions.assertTrue(isFocusOnBody());
    }

    private boolean isFocusOnTree() {
        return jq("@tree").attr("id").equals(getEval("document.activeElement.id"))
                && "DIV".equals(getEval("document.activeElement.tagName"));
    }

    private boolean isFocusOnBody() {
        return "".equals(getEval("document.activeElement.id"))
                && "BODY".equals(getEval("document.activeElement.tagName"));
    }
}
