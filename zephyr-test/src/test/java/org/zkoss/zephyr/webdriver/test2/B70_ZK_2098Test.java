package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2098Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button:eq(1)"));
		waitResponse();
		assertNoAnyError();
		JQuery treeCols = jq("@treecols @treecol");
		JQuery treeCells = jq("@treerow @treecell");
		for (int i = 0; i < 5; i++) {
			int width = treeCols.eq(i).width();
			assertNotEquals(0, width);
			assertEquals(width, treeCells.eq(i).width(), 1);
		}
	}
}
