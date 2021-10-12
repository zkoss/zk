/* B95_ZK_4766Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 13 15:29:38 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */

@Category(WcagTestOnly.class)
public class B95_ZK_4766Test extends WebDriverTestCase {

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqAfter1 = jq("$after1");
		JQuery jqAfter2 = jq("$after2");
		//ZK-4767
		Assert.assertEquals(jq("$right1").parent().attr("id"), jqAfter1.attr("aria-controls"));
		JQuery jqSplitter = jq(jqAfter2.toWidget().$n("splitter"));

		//ZK-4768
		Assert.assertEquals(jq("$right2").parent().attr("id"), jqSplitter.attr("aria-controls"));

		//ZK-4766, ZK-4769
		click(jqAfter1.toWidget().$n("btn"));
		Assert.assertEquals("0", jqAfter1.attr("aria-valuenow"));
		Assert.assertEquals("0", jqAfter1.attr("aria-valuetext"));

		//ZK-4770
		click(jqAfter2.toWidget().$n("splitter-btn"));
		Assert.assertEquals("0", jqSplitter.attr("aria-valuenow"));
		Assert.assertEquals("0", jqSplitter.attr("aria-valuetext"));
	}
}
