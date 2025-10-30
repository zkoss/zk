package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B80_ZK_3299Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq("@button").get(0));
        waitResponse(true);
		assertEquals("true,true", getZKLog());
		closeZKLog();
		waitResponse(true);
		click(jq("@button").get(1));
		waitResponse(true);
		assertEquals("false,false", getZKLog());
		closeZKLog();
		waitResponse(true);
		click(jq("@button").get(2));
		waitResponse(true);
		assertEquals("true,false", getZKLog());
		closeZKLog();
		waitResponse(true);
		click(jq("@button").get(3));
		waitResponse(true);
		assertEquals("true,true", getZKLog());
		closeZKLog();
		waitResponse(true);
		click(jq("@button").get(4));
		waitResponse(true);
		assertEquals("false,false", getZKLog());
		closeZKLog();
    }

}
