package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author Jameschu
 */
public class B70_ZK_2532Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
		JQuery tbs = jq("@textbox");
		assertEquals(3, tbs.length());
		for (int i = 0; i < tbs.length(); i++) {
			JQuery $textbox = tbs.eq(i);
			focus($textbox);
			waitResponse();
			type($textbox, "a");
			waitResponse();
			blur($textbox);
			waitResponse();
		}
		String log = getZKLog();
        assertEquals(true, log.contains("onOK") && log.contains("onCancel") && log.contains("onCtrlKey"));
    }
}
