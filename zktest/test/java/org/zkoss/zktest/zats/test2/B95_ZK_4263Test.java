/* B95_ZK_4263Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 15 15:27:42 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B95_ZK_4263Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		final JQuery listgroup = jq("@listgroup:eq(0)");
		click(widget(listgroup).$n("img"));
		waitResponse();
		Assert.assertFalse("The listgroup is still opened", listgroup.hasClass("z-listgroup-open"));

		click(widget(listgroup).$n("img"));
		waitResponse();
		Assert.assertTrue("The listgroup is still opened", listgroup.hasClass("z-listgroup-open"));
	}
}
