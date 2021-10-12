/* F86_ZK_4122Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Nov 08 17:14:50 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_4122Test extends WebDriverTestCase {
	private final static String orgchildrenClass = ".z-orgchildren";

	@Test
	public void test() {
		connect();

		JQuery orgchildren = findOrgitem("Item1").find(orgchildrenClass);
		Assert.assertFalse(orgchildren.children().exists());

		click(findButton("appendOrgitem"));
		waitResponse();
		click(findButton("toggleItem1"));
		waitResponse();
		Assert.assertEquals(3, orgchildren.children().length());

		JQuery item2 = findOrgitem("Item2");
		orgchildren = item2.find(orgchildrenClass);
		Assert.assertFalse(orgchildren.children().exists());

		JQuery icon = item2.find(".z-orgnode-icon");
		click(icon);
		waitResponse();
		Assert.assertEquals(2, orgchildren.children().length());

		click(icon);
		waitResponse();
		Assert.assertEquals(2, orgchildren.children().length());
		Assert.assertFalse(orgchildren.isVisible());

		click(findButton("appendOrgchildren"));
		waitResponse();
		orgchildren = findOrgitem("Item5").find(orgchildrenClass);
		Assert.assertFalse(orgchildren.children().exists());

		click(findButton("toggleItem5"));
		waitResponse();
		Assert.assertEquals(2, orgchildren.children().length());
	}

	private JQuery findOrgitem(String label) {
		return jq(".z-orgnode:contains(" + label + ")").parent();
	}

	private JQuery findButton(String label) {
		return jq(".z-button:contains(" + label + ")");
	}
}
