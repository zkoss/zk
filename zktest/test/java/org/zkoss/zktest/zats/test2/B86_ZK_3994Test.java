/* B86_ZK_3994Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 20 18:34:15 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_3994Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testMeshWidgetHide("@listheader", "@listcell");
		testMeshWidgetHide("@column", "@cell");
		testMeshWidgetHide("@treecol", "@treecell");

		click(jq("@button"));
		waitResponse();

		testMeshWidgetShow("@listheader", "@listcell");
		testMeshWidgetShow("@column", "@cell");
		testMeshWidgetShow("@treecol", "@treecell");

		click(jq("@button"));
		waitResponse();

		testMeshWidgetHide("@listheader", "@listcell");
		testMeshWidgetHide("@column", "@cell");
		testMeshWidgetHide("@treecol", "@treecell");
	}

	private void testMeshWidgetHide(String header, String column) {
		assertEquals(header + " h1 should be invisible", 0, getWidth(jq(header + ":contains(h1)")));
		assertNotEquals(header + " h2 should be visible", 0, getWidth(jq(header + ":contains(h2)")));
		assertEquals(column + " c1 should be invisible", 0, getWidth(jq(column + ":contains(c1)")));
		assertNotEquals(column + " c2 should be visible", 0, getWidth(jq(column + ":contains(c2)")));
	}

	private void testMeshWidgetShow(String header, String column) {
		assertNotEquals(header + " h1 should be visible", 0, getWidth(jq(header + ":contains(h1)")));
		assertNotEquals(header + " h2 should be visible", 0, getWidth(jq(header + ":contains(h2)")));
		assertEquals(column + " c1 should be invisible", 0, getWidth(jq(column + ":contains(c1)")));
		assertNotEquals(column + " c2 should be visible", 0, getWidth(jq(column + ":contains(c2)")));
	}

	private int getWidth(JQuery elem) {
		return Integer.parseInt(elem.toElement().get("clientWidth"));
	}
}
