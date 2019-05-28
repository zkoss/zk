/* B86_ZK_4254Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 28 18:18:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4254Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testListbox(jq("@listbox:eq(0)"));
		testListbox(jq("@listbox:eq(2)"));
	}

	private void testListbox(JQuery listbox) {
		JQuery verScrollbar = listbox.find(".z-scrollbar-vertical-embed");
		int oldLeft = verScrollbar.positionLeft();
		listbox.find(".z-listbox-body").scrollLeft(100);
		Assert.assertEquals("Vertical scroll position changed!", oldLeft, verScrollbar.positionLeft());
	}
}
