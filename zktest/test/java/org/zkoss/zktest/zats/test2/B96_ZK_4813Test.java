/* B96_ZK_4813Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 09 12:50:22 CST 2021, Created by jameschu

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
public class B96_ZK_4813Test extends WebDriverTestCase {

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqSplitter = jq(".z-splitter");
		click(jqSplitter);
		waitResponse();
		Assert.assertEquals("0", jqSplitter.attr("aria-valuenow"));
		jqSplitter = jq(".z-splitlayout-splitter");
		click(jqSplitter);
		waitResponse();
		Assert.assertEquals("0", jqSplitter.attr("aria-valuenow"));
	}
}
