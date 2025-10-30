/* B86_ZK_3883Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 31 10:49:17 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_3883Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		checkAlign();
		click(jq("$fireResize"));
		waitResponse();
		checkAlign();
	}

	public void checkAlign() {
		int treecolA = jq("$myTreeA .z-treecol").width();
		int treecellA = jq("$myTreeA .z-treecell").width();
		int treefooterA = jq("$myTreeA .z-treefooter").width();
		System.out.println(treecolA + "," + treecellA + "," + treefooterA);
		assertEquals(treecolA, treecellA, 1);
		assertEquals(treecolA, treecolA, 1);
		assertEquals(treecolA, treefooterA, 1);

		int listheaderA = jq("$myListboxA .z-listheader").width();
		int listcellA = jq("$myListboxA .z-listcell").width();
		int listfooterA = jq("$myListboxA .z-listfooter").width();
		assertEquals(listheaderA, listcellA, 1);
		assertEquals(listheaderA, listfooterA, 1);
		assertEquals(listcellA, listfooterA, 1);

		int columnA = jq("$myGridA .z-column").width();
		int rowinnerA = jq("$myGridA .z-row-inner").width();
		int footerA = jq("$myGridA .z-footer").width();
		assertEquals(columnA, rowinnerA, 1);
		assertEquals(columnA, footerA, 1);
		assertEquals(rowinnerA, footerA, 1);

		int treecolB = jq("$myTreeB .z-treecol").width();
		int treecellB = jq("$myTreeB .z-treecell").width();
		int treefooterB = jq("$myTreeB .z-treefooter").width();
		assertEquals(treecolB, treecellB, 1);
		assertEquals(treecolB, treefooterB, 1);
		assertEquals(treecellB, treefooterB, 1);

		int listheaderB = jq("$myListboxB .z-listheader").width();
		int listcellB = jq("$myListboxB .z-listcell").width();
		int listfooterB = jq("$myListboxB .z-listfooter").width();
		assertEquals(listheaderB, listcellB, 1);
		assertEquals(listheaderB, listfooterB, 1);
		assertEquals(listcellB, listfooterB, 1);

		int columnB = jq("$myGridB .z-column").width();
		int rowinnerB = jq("$myGridB .z-row-inner").width();
		int footerB = jq("$myGridB .z-footer").width();
		assertEquals(columnB, rowinnerB, 1);
		assertEquals(columnB, footerB, 1);
		assertEquals(rowinnerB, footerB, 1);
	}
}
