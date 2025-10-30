/* B85_ZK_3921Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 19 10:48:47 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3921Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testWidget(jq(".z-grid-body"), jq(".z-button:eq(0)"));
		testWidget(jq(".z-listbox-body"), jq(".z-button:eq(2)"));
		testWidget(jq(".z-tree-body"), jq(".z-button:eq(4)"));
	}

	private void testWidget(JQuery widget, JQuery button) {
		verScroll(widget, 33);
		waitResponse();

		int scrollTop = widget.scrollTop();

		click(button);
		waitResponse();

		Assertions.assertEquals(scrollTop, widget.scrollTop(), 2);

		verScroll(widget, 66);
		waitResponse();

		scrollTop = widget.scrollTop();

		click(button.next());
		waitResponse();

		Assertions.assertEquals(scrollTop, widget.scrollTop(), 2);
	}

	private void verScroll(JQuery widget, int percentage) {
		int scrollTopMax = widget.scrollHeight() - widget.innerHeight();
		widget.scrollTop(scrollTopMax * percentage / 100);
	}
}
