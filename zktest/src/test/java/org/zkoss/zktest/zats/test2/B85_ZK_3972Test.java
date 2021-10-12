/* B85_ZK_3972Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Jun 25 15:23:50 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.ZATSTestCase;

public class B85_ZK_3972Test extends ZATSTestCase {

    @Test
    public void test() {
        connect();
        try {
            connect("/test2/B85-ZK-3972-2.zul");
        } catch (Exception e) {
            throw e;
        }
    }
}
