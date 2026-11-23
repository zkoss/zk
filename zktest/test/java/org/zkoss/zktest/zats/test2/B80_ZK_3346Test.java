/* B80_ZK_3346Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 16:52:10 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B80_ZK_3346Test extends WebDriverTestCase {

	private List<JQuery> widgets;

	@Test
	public void test() {
		connect();
		widgets = Arrays.asList(
				jq(jq(".z-label:contains(click a label)")),
				jq(".z-button:eq(2)"),
				jq(".z-textbox"),
				jq(".z-div"),
				jq(".z-label:contains(click the background)"));
		testPopup(jq(".z-button:eq(0)"), jq(".z-popup"));
		testPopup(jq(".z-button:eq(1)"), jq(".z-menupopup"));
		testPopup(jq(".z-combobox-button"), jq(".z-combobox-popup"));
	}

	private void testPopup(JQuery button, JQuery popup) {
		widgets.forEach(widget -> {
			click(button);
			waitResponse();
			Assert.assertTrue(popup.isVisible());
			click(widget);
			waitResponse();
			Assert.assertFalse(popup.isVisible());
		});
	}
}
