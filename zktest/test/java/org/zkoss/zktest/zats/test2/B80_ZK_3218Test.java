package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

public class B80_ZK_3218Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
		click(jq("$btn"));
		waitResponse();
		assertEquals(">>0.001", getZKLog());
    }
}
