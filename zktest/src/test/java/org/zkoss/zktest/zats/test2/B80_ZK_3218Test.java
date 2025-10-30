package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



public class B80_ZK_3218Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
		click(jq("$btn"));
		waitResponse();
		assertEquals(">>0.001", getZKLog());
    }
}
