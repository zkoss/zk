package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
				"tree init tree1 tree init tree2 listbox init list1 listbox init list1 " +
				"listbox init list2 grid init grid1 grid init grid1 grid init grid2 " +
				"tree init tree1 tree init tree2";
        assertEquals(log, getZKLog().trim());
    }
}
