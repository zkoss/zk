/* B96_ZK_5353Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 07 16:52:12 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */

public class B96_ZK_5353Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		type(jq("@textbox"), "1");
		waitResponse();
		Assert.assertFalse(hasError());
	}

	@Test
	public void test1() throws Exception {
		connect("/test2/B96-ZK-5353-1.zul");
		waitResponse();
		JQuery jqBtn = jq("@button");
		click(jqBtn.eq(0));
		click(jqBtn.eq(1));
		waitResponse();
		Assert.assertFalse(hasError());
	}
}
