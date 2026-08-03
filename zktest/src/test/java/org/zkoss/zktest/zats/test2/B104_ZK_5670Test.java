/* B104_ZK_5670Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 20 14:14:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B104_ZK_5670Test extends WebDriverTestCase {
    @RegisterExtension
    public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B104-ZK-5670.xml");

    @Test
    public void testEmbeddedProgressboxPosition() {
        connect("/test2/B104-ZK-5670Embedded.zhtml");
        waitResponse();
        sleep(2000);
        String progPos = getEval("typeof zk !== 'undefined' && zk.progPos ? zk.progPos : 'not set'");
        assertEquals("center, center", progPos);
    }

    @Test
    public void testNonEmbeddedProgressboxPosition() {
        connect("/test2/B104-ZK-5670.zul");
        waitResponse();
        String progPos = getEval("typeof zk !== 'undefined' && zk.progPos ? zk.progPos : 'not set'");
        assertEquals("center, center", progPos);
    }
}
