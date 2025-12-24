/* B103_ZK_5970Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 24 15:54:14 CST 2025, Created by josephlo

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5970Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        click(widget("@nav[label=\"nav2\"]"));
        click(widget("@button[label=\"collapse nav\"]"));
        assertNoJSError();
    }
}