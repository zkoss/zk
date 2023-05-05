/* B60_ZK_1305Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 12 16:42:55 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.TouchWebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B60_ZK_1305Test extends TouchWebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery test0 = jq(".z-listitem:contains(test0)");
		click(test0);
		waitResponse();

		Assert.assertEquals("select index: 0", jq("$lbl").text());

		JQuery body = jq(".z-listbox-body");
		scroll(toElement(jq(".z-listbox")), 0, 200);
		waitResponse();
		Assert.assertNotEquals(0, body.scrollTop());
	}
}
