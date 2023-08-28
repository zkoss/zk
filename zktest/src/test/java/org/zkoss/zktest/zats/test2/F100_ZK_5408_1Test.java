/* F100_ZK_5408_1Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Aug 28 13:24:19 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zkmax.au.InaccessibleWidgetBlockService;
import org.zkoss.zktest.zats.ZATSTestCase;

@ForkJVMTestOnly
public class F100_ZK_5408_1Test extends ZATSTestCase {

    // Disable InaccessibleWidgetBlockService by set library-property `org.zkoss.zkmax.au.IWBS.disable` to `true`
    @RegisterExtension
    public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F100-ZK-5408-1-zk.xml");

    @Test
    public void test() {
        Assertions.assertTrue(InaccessibleWidgetBlockService.isDisable());
    }
}
