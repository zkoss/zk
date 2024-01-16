/* F100_ZK_3853_3Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 02 19:34:54 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ztl.Widget;

public class F100_ZK_3853_3Test extends F100_ZK_3853_1Test {

	/*
		mode 0: none
		mode 1: checkmark
		mode 2: multiple
		mode 3: checkmark + multiple
		mode 4: tristate ( + checkmark + multiple )
	*/

	@Test
	public void none_to_tristate() {
		connect();

		openAll();

		click(jq("@treerow:eq(9)"));
		waitResponse();
		toggleTristate(); // open

		assertAttrs(true, true, true);

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 9);
		assertCm(0, 0, 1, 2, 6, 7, 10, 11, 12, 13);
	}

	@Test
	public void checkmark_to_tristate() {
		connect();

		openAll();

		toggleCheckmark(); // open
		assertAttrs(true, false, false);
		click(jq("@treerow:eq(9)"));
		waitResponse();

		toggleTristate(); // open
		assertAttrs(true, true, true);

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 9);
		assertCm(0, 0, 1, 2, 6, 7, 10, 11, 12, 13);
	}

	@Test
	public void multiple_to_tristate() {
		connect();

		openAll();

		toggleMultiple(); // open
		assertAttrs(false, true, false);
		click(jq("@treerow:eq(9)"));
		waitResponse();

		toggleTristate(); // open
		assertAttrs(true, true, true);

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 9);
		assertCm(0, 0, 1, 2, 6, 7, 10, 11, 12, 13);
	}

	@Test
	public void checkmark_and_multiple_to_tristate() {
		connect();

		openAll();

		toggleCheckmark(); // open
		assertAttrs(true, false, false);
		toggleMultiple(); // open
		assertAttrs(true, true, false);

		clickCm(2, 9);

		toggleTristate(); // open
		assertAttrs(true, true, true);

		assertHdcm(2);
		assertCm(2, 0, 4, 5, 8);
		assertCm(1, 2, 9);
		assertCm(0, 1, 6, 7, 10, 11, 12, 13);
	}

	@Test
	public void tristate_to_checkmark_and_multiple() {
		connect();

		openAll();

		toggleTristate(); // open
		assertAttrs(true, true, true);

		click(jq("@treerow:eq(9)"));
		waitResponse();

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 9);
		assertCm(0, 0, 1, 2, 6, 7, 10, 11, 12, 13);

		toggleTristate(); // close
		assertAttrs(true, true, false);

		assertHdcm(0);
		for (int i = 0; i <= 13; ++i) {
			if (i == 9) assertCm(1, 9);
			else assertCm(0, i);
		}
	}

	public void toggleCheckmark() {
		click(jq("@button:eq(0)"));
		waitResponse();
	}

	public void toggleMultiple() {
		click(jq("@button:eq(1)"));
		waitResponse();
	}

	public void toggleTristate() {
		click(jq("@button:eq(2)"));
		waitResponse();
	}

	/* for model testcases (F100_ZK_3853_4Test)*/
	public void openAll() {
		while (jq(".z-tree-close").length() != 0) {
			click(jq(".z-tree-close"));
			waitResponse();
		}
	}

	public void assertAttrs(boolean checkmark, boolean multiple, boolean tristate) {
		Widget tree = widget(jq("@tree:eq(0)"));
		assertEquals(checkmark, tree.is("checkmark"));
		assertEquals(multiple, tree.is("multiple"));
		assertEquals(tristate, tree.is("tristate"));
	}

	@Override
	public void test() {
		// don't run super's test()
		// inherit just for the methods inside
	}
}
