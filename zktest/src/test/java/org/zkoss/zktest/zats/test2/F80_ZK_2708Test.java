package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openqa.selenium.Keys;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F80_ZK_2708Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery rowlayout = jq(".z-rowlayout").eq(0);
		int totalWidth = rowlayout.outerWidth();
		int childrenWidth = 0;
		for (int i = 0; i < rowlayout.children().length(); i++) {
			JQuery rowchildren = rowlayout.children().eq(i);
			if (i > 0) {
				JQuery rowchildrenpre = rowlayout.children().eq(i - 1);
				assertTrue(Math.abs(rowchildren.width() - rowchildrenpre.width()) < 2);
			}
			childrenWidth += rowchildren.outerWidth(true);
		}
		assertTrue(Math.abs(totalWidth - childrenWidth) < 10);

		rowlayout = jq(".z-rowlayout").eq(6);
		totalWidth = rowlayout.outerWidth();
		childrenWidth = 0;
		for (int i = 0; i < rowlayout.children().length(); i++) {
			JQuery rowchildren = rowlayout.children().eq(i);
			if (i > 0) {
				JQuery rowchildrenpre = rowlayout.children().eq(i - 1);
				assertTrue(Math.abs(rowchildren.width() - rowchildrenpre.width()) < 2);
				//test offset
				assertTrue(rowchildren.outerWidth(true) < rowchildrenpre.outerWidth(true));
			}
			childrenWidth += rowchildren.outerWidth(true);
		}
		assertTrue(Math.abs(totalWidth - childrenWidth) < 10);

		rowlayout = jq(".z-rowlayout").eq(9);
		totalWidth = rowlayout.width();
		childrenWidth = 0;
		for (int i = 0; i < rowlayout.children().length(); i++) {
			JQuery rowchildren = rowlayout.children().eq(i);
			if (i > 0) {
				JQuery rowchildrenpre = rowlayout.children().eq(i - 1);
				assertTrue(Math.abs(rowchildren.width() - rowchildrenpre.width()) < 2);
			}
			childrenWidth += rowchildren.outerWidth(true);
		}
		assertTrue(Math.abs(totalWidth - childrenWidth) < 12); //due to 1px offset

		JQuery spinner = jq(".z-spinner-input");
		sendKeys(spinner, Keys.ARROW_DOWN);
		waitResponse();
		totalWidth = rowlayout.outerWidth();
		childrenWidth = 0;
		assertTrue(rowlayout.children().length() == 11);
		for (int i = 0; i < rowlayout.children().length(); i++) {
			JQuery rowchildren = rowlayout.children().eq(i);
			if (i > 0) {
				JQuery rowchildrenpre = rowlayout.children().eq(i - 1);
				assertTrue(Math.abs(rowchildren.width() - rowchildrenpre.width()) < 2);
			}
			childrenWidth += rowchildren.outerWidth(true);
		}
		assertTrue(Math.abs(totalWidth - childrenWidth) < 10);
	}
}