/* B103_ZK_5571_1Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 08 14:19:03 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B103_ZK_5571_1Test extends ZATSTestCase {
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