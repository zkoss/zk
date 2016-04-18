package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

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
		assertEquals("02100true false", getZKLog());
	}

}