/* B90_ZK_4403Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 4 19:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4403Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		JQuery steps = jq("@step");
		for (int i = 0, l = steps.length(); i < l; i++) {
			JQuery next = jq(steps.eq(i).toElement().nextSibling());
			if (next.length() == 0) break;
			Assert.assertTrue("index: " + i + ", next: " + next.attr("class"), next.hasClass("z-stepbar-separator"));
		}
	}
}
