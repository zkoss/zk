package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B30_1859568Test extends WebDriverTestCase {
	private int[] posX = new int[] { 0, 0, 0, 0 };
	private int[] posY = new int[] { 0, 0, 0, 0 };
	private String[] labels = new String[] { "", "", "", "" };

	@Test
	public void test() {
		connect();
		checkLabels(true);
		JQuery cbs = jq(".z-listitem-checkbox");
		for (int i = 0; i < cbs.length(); i++) {
			click(cbs.eq(i));
			waitResponse();
			checkLabels(false);
			click(cbs.eq(i));
			waitResponse();
			checkLabels(false);
		}
	}

	public void checkLabels(boolean init) {
		JQuery tbtns = jq("@toolbarbutton");
		for (int i = 0; i < tbtns.length(); i++) {
			JQuery tbtn = tbtns.eq(i);
			if (init) {
				posX[i] = tbtn.positionLeft();
				posY[i] = tbtn.positionTop();
				labels[i] = tbtn.toWidget().get("label");
			} else {
				assertEquals(posX[i], tbtn.positionLeft());
				assertEquals(posY[i], tbtn.positionTop());
				assertEquals(labels[i], tbtn.toWidget().get("label"));
			}
		}
	}
}