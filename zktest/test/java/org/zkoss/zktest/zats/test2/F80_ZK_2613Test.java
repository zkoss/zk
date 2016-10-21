package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author Jameschu
 */
public class F80_ZK_2613Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
		waitResponse(1000);
		String log = getZKLog();
        assertEquals(true, log.contains("/zktest/zkau/web/") && log.contains("/img/spacer.gif"));
    }
}
