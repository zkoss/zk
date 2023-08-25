/* F100_ZK_5408Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Aug 28 12:00:52 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zkoss.zkmax.au.InaccessibleWidgetBlockService;
import org.zkoss.zktest.zats.ZATSTestCase;

public class F100_ZK_5408Test extends ZATSTestCase {

    // Enable InaccessibleWidgetBlockService by default

    @Test
    public void test() {
        Assertions.assertFalse(InaccessibleWidgetBlockService.isDisable());
    }
}
