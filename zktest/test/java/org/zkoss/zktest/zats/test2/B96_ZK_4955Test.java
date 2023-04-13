/* B96_ZK_4955Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 13 14:22:31 CST 2023, Created by jameschu

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

public class B96_ZK_4955Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqComboInput = jq("@combobox input");
		sendKeys(jqComboInput, "0");
		waitResponse();
		blur(jqComboInput);
		waitResponse();
		Assert.assertEquals("Onchange: selection -> 1", getZKLog());
		closeZKLog();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("selection count: 1", getZKLog());
	}
}
