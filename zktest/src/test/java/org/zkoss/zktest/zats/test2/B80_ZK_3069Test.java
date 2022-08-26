package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * Created by wenning on 4/15/16.
 */
public class B80_ZK_3069Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		sleep(1000);
		assertEquals("02100true", getZKLog());
		click(jq("@button").get(1));
		waitResponse(true);
		assertEquals("02100true\nfalse", getZKLog());
	}

}