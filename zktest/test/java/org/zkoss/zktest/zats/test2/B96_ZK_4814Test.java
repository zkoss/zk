/* B96_ZK_4814Test.java

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
public class B96_ZK_4814Test extends WebDriverTestCase {

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqSplitter = jq(".z-splitter");
		String valuenow = jqSplitter.attr("aria-valuenow");
		dragdropTo(jqSplitter, 3, 0, 20, 0);
		waitResponse();
		Assert.assertNotEquals(jqSplitter.attr("aria-valuenow"), valuenow);
	}
}
