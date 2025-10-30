package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B86_ZK_4359Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btns = jq("@button");
		assertTrue(jq("$c1").toElement().get("className").contains("z-column-sort"));
		assertTrue(jq("$l1").toElement().get("className").contains("z-listheader-sort"));
		assertTrue(jq("$t1").toElement().get("className").contains("z-treecol-sort"));
		click(btns.eq(1));
		waitResponse();
		assertFalse(jq("$c1").toElement().get("className").contains("z-column-sort"));
		assertFalse(jq("$l1").toElement().get("className").contains("z-listheader-sort"));
		assertFalse(jq("$t1").toElement().get("className").contains("z-treecol-sort"));
		click(btns.eq(0));
		waitResponse();
		assertTrue(jq("$c1").toElement().get("className").contains("z-column-sort"));
		assertTrue(jq("$l1").toElement().get("className").contains("z-listheader-sort"));
		assertTrue(jq("$t1").toElement().get("className").contains("z-treecol-sort"));
		assertEquals(0, jq(".z-icon-caret-up").length());
		assertEquals(0, jq(".z-icon-caret-down").length());
		assertFalse(jq("$c2").toElement().get("className").contains("z-column-sort"));
		assertFalse(jq("$l2").toElement().get("className").contains("z-listheader-sort"));
		assertFalse(jq("$t2").toElement().get("className").contains("z-treecol-sort"));
		click(btns.eq(2));
		waitResponse();
		assertTrue(jq("$c2").toElement().get("className").contains("z-column-sort"));
		assertTrue(jq("$l2").toElement().get("className").contains("z-listheader-sort"));
		assertTrue(jq("$t2").toElement().get("className").contains("z-treecol-sort"));
		click(btns.eq(3));
		waitResponse();
		assertFalse(jq("$c2").toElement().get("className").contains("z-column-sort"));
		assertFalse(jq("$l2").toElement().get("className").contains("z-listheader-sort"));
		assertFalse(jq("$t2").toElement().get("className").contains("z-treecol-sort"));
	}
}
