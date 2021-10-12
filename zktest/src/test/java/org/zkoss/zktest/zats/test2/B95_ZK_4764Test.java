/* B95_ZK_4764Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 14 16:29:38 CST 2021, Created by jameschu

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

public class B95_ZK_4764Test extends WebDriverTestCase {

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		int $btn2 = jq("$btn2").width();
		Assert.assertEquals(jq("$btn1").width(), $btn2, 1);
		Assert.assertEquals($btn2, jq("$btn3").width(), 1);
	}
}
