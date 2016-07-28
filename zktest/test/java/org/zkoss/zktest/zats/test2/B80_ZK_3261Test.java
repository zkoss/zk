package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenninghsu on 7/28/16.
 */
public class B80_ZK_3261Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        sleep(10000);
        assertEquals("popupmenu onHide", getZKLog());
    }

}
