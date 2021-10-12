/* B85_ZK_3965Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 15 12:57:53 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3965Test extends WebDriverTestCase {
	@Test
	public void testModel() {
		connect();

		assertTrue("s1 not selected", isSelected(jq("@radio:contains(s1)")));

		click(jq("@button:contains(add s3 to model)"));
		waitResponse();
		assertTrue("s3 not attached", jq("@radiogroup").find("@radio:contains(s3)").exists());
	}

	@Test
	public void testExternal() {
		connect();

		assertTrue("e6 not selected", isSelected(jq("@radio:contains(e6)")));

		click(jq("@button:contains(add an external radio)"));
		waitResponse();
		assertFalse("e6 still selected", isSelected(jq("@radio:contains(e6)")));
		assertTrue("new radio not selected", isSelected(jq("@radio:last")));
	}

	private boolean isSelected(JQuery radio) {
		return radio.find("> :radio").is(":checked");
	}
}
