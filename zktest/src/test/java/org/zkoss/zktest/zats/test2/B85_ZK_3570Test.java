/* B85_ZK_3570Test.java

    Purpose:

    Description:

    History:
            Thu Mar 08 11:03:36 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B85_ZK_3570Test extends ZATSTestCase {

    @Test
    public void test() {
        DesktopAgent desktop = connect();

        tryCatchNullPointerException(desktop.queryAll("#lb button"));

        tryCatchNullPointerException(desktop.queryAll("#gr button"));
    }

    private void tryCatchNullPointerException(List<ComponentAgent> buttonList) {
        try {
            buttonList.get(buttonList.size() - 1).click();
        } catch (NullPointerException e) {
            Assertions.fail();
        }
    }
}
