package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

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
