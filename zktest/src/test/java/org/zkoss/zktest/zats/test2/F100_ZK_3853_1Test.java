/* F100_ZK_3853_1Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 02 16:06:35 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F100_ZK_3853_1Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		/*  all `open` tags are initially close */

		/* Test hdcm turn to partial state */
		clickCm(2); // check (4)

		assertHdcm(2);
		assertCm(0, 0, 1);
		assertCm(1, 2);

		/* Test hdcm turn to checked state */
		clickCm(0, 1); // check (0, 1)

		assertHdcm(1);
		assertCm(1, 0, 1, 2);

		/* Test hdcm turn to un-checked state */
		clickCm(0, 1, 2); // un-check (0, 3, 4)

		assertHdcm(0);
		assertCm(0, 0, 1, 2);

		/* Test check a close tag will update all its descendants to checked state */
		clickOpen(0, 1, 2, 3); // open (0, 4, 5, 8)
		clickCm(4); // check (4)

		assertHdcm(2);
		for (int i = 4; i <= 13; ++i) assertCm(1, i);

		/* Test un-checked a cm inside a all-selected cm will show partial state */
		clickCm(10); // un-check (10)

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 6, 7, 9, 11, 12, 13);
		assertCm(0, 0, 1, 2, 3, 10);

		/* Test another cm shows partial state in its ancestors */
		clickCm(1); // check (1)

		assertHdcm(2);
		assertCm(2, 0, 4, 5, 8);
		assertCm(1, 1, 6, 7, 9, 11, 12, 13);
		assertCm(0, 2, 3, 10);

		/* Test head cm check-all and clear all partial states */
		clickHdcm();

		assertHdcm(1);
		for (int i = 0; i <= 13; ++i) assertCm(1, i);

		/* Test head cm un-check-all */
		clickHdcm();

		assertHdcm(0);
		for (int i = 0; i <= 13; ++i) assertCm(0, i);

		/* Test head cm update correctly when check cm from deep to shallow */
		clickCm(10, 7, 3);

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 3, 7, 10);
		assertCm(0, 0, 1, 2, 6, 9, 11, 12, 13);

		clickHdcm();

		assertHdcm(1);
		for (int i = 0; i <= 13; ++i) assertCm(1, i);

		clickHdcm();

		assertHdcm(0);
		for (int i = 0; i <= 13; ++i) assertCm(0, i);

		/* Test head cm update correctly when check cm from shallow to deep */
		clickCm(3, 7, 10);

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 3, 7, 10);
		assertCm(0, 0, 1, 2, 6, 9, 11, 12, 13);

		clickHdcm();

		assertHdcm(1);
		for (int i = 0; i <= 13; ++i) assertCm(1, i);

		clickHdcm();

		assertHdcm(0);
		for (int i = 0; i <= 13; ++i) assertCm(0, i);
	}

	public void clickCm(int... index) {
		for (int idx : index) {
			click(jq("@treerow:eq(" + idx + ") .z-treerow-checkable"));
			waitResponse();
		}
	}

	public void clickHdcm() {
		click(jq("@treecol .z-treecol-checkable"));
		waitResponse();
	}

	public void clickOpen(int... index) {
		for (int idx : index) {
			click(jq(".z-tree-icon:eq(" + idx + ")"));
			waitResponse();
		}
	}

	public void assertCm(int state, int ... cmIndex) {
		for (int idx : cmIndex) {
			String trClassname = jq("@treerow:eq(" + idx + ")").toElement().get("className"),
					iconClassname = jq("@treerow:eq(" + idx + ") .z-treerow-icon").toElement().get("className");
			if (state == 2) {
				assertTrue(trClassname.contains("z-treerow-partial"));
				assertTrue(iconClassname.contains("z-icon-minus"));
				assertFalse(trClassname.contains("z-treerow-selected"));
				assertFalse(iconClassname.contains("z-icon-check"));
			} else if (state == 1) {
				assertTrue(trClassname.contains("z-treerow-selected"));
				assertTrue(iconClassname.contains("z-icon-check"));
				assertFalse(trClassname.contains("z-treerow-partial"));
				assertFalse(iconClassname.contains("z-icon-minus"));
			} else {
				assertTrue(iconClassname.contains("z-icon-check"));
				assertFalse(trClassname.contains("z-treerow-selected"));
				assertFalse(trClassname.contains("z-treerow-partial"));
				assertFalse(iconClassname.contains("z-icon-minus"));
			}
		}
	}

	public void assertHdcm(int state) {
		JQuery hdcmClassname = jq(".z-treecol .z-treecol-checkable"),
				iconClassname = jq(".z-treecol .z-treecol-checkable .z-treecol-icon");
		if (state == 2) {
			assertTrue(hdcmClassname.hasClass("z-treecol-partial"));
			assertTrue(iconClassname.hasClass("z-icon-minus"));
			assertFalse(hdcmClassname.hasClass("z-treecol-checked"));
			assertFalse(iconClassname.hasClass("z-icon-check"));
		} else if (state == 1) {
			assertTrue(hdcmClassname.hasClass("z-treecol-checked"));
			assertTrue(iconClassname.hasClass("z-icon-check"));
			assertFalse(hdcmClassname.hasClass("z-treecol-partial"));
			assertFalse(iconClassname.hasClass("z-icon-minus"));
		} else {
			assertTrue(iconClassname.hasClass("z-icon-check"));
			assertFalse(hdcmClassname.hasClass("z-treecol-checked"));
			assertFalse(hdcmClassname.hasClass("z-treecol-partial"));
			assertFalse(iconClassname.hasClass("z-icon-minus"));
		}
	}
}
