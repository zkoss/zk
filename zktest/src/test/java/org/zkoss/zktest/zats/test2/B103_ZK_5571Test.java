/* B103_ZK_5571Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 24 14:06:27 CST 2025, Created by josephlo

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B103_ZK_5571Test extends ZATSTestCase {
    @Test
    public void test() {
        try {
            connect();
        } catch (Throwable e) {
            Throwable cause = e.getCause();
            Assertions.assertInstanceOf(UiException.class, cause);
        }
    }
}