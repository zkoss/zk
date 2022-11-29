package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2128Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery treeCells = jq(".z-treecell");
		int treeCellCount = treeCells.length();
		for (int i = 0; i <= 1; i++) {
			JQuery btn = jq(".z-button").eq(i);
			for (int j = 0; j <= 1; j++) {
				click(btn);
				waitResponse();
				assertEquals(treeCellCount, treeCells.length());
				for (int k = 0; k < 0; k++) {
					assertTrue(treeCells.eq(k).isVisible());
					assertTrue(treeCells.eq(k).width() > 10);
				}
			}
		}
	}
}
