package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 5/15/16.
 */
public class B80_ZK_3115Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        assertEquals(Integer.parseInt(jq("@macro").get(0).eval("offsetTop")) + Integer.parseInt(jq("@macro").get(0).eval("offsetHeight")), Integer.parseInt(jq("@macro").get(1).eval("offsetTop")));
    }

}
