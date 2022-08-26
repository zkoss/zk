package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author Jameschu
 */
public class B70_ZK_2603Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
		JQuery btns = jq("@button");
		assertEquals(6, btns.length());
		for (int i = 0; i < btns.length(); i++) {
			click(btns.eq(i));
			waitResponse();
		}
		String log = "listbox init list1 listbox init list2 grid init grid1 grid init grid2 " +
				"tree init tree1 tree init tree2 listbox init list1 listbox init list2 " +
				"listbox init list1 grid init grid1 grid init grid2 grid init grid1 " +
				"tree init tree1 tree init tree2";
        assertEquals(log, getZKLog().trim().replaceAll("\n", " "));
    }
}
