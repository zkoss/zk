/* B90_ZK_4401_4Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 15:39:38 CST 2019, Created by jameschu

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
public class B90_ZK_4401_4Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery jqBtn = jq("@button");
		JQuery jqResultDiv = jq("@div");
		click(jqBtn.eq(0));
		waitResponse();
		Assert.assertEquals("3", jqResultDiv.find("@label").eq(3).html().trim());
		click(jqBtn.eq(1));
		waitResponse();
		Assert.assertEquals("3.1", jqResultDiv.find("@label").eq(4).html().trim());
		click(jqBtn.eq(2));
		waitResponse();
		Assert.assertEquals("3.2", jqResultDiv.find("@label").last().prev().html().trim());
		click(jqBtn.eq(3));
		waitResponse();
		Assert.assertEquals("4", jqResultDiv.find("@label").last().prev().html().trim());
		click(jqBtn.eq(4));
		waitResponse();
		Assert.assertEquals("3.1.3", jqResultDiv.find("@label").eq(7).html().trim());
		click(jqBtn.eq(5));
		waitResponse();
		Assert.assertEquals("3.1.1.1", jqResultDiv.find("@label").eq(6).html().trim());
	}
}
