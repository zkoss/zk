/* B85_ZK_3843Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 06 14:32:15 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3843Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("$listbox1").toWidget().firstChild());
		waitResponse();
		Assert.assertTrue(jq("@notification").exists());
		waitResponse();
		click(jq("body"));
		waitResponse();
		click(jq("$listbox2").toWidget().firstChild());
		waitResponse();
		Assert.assertTrue(jq("@notification").exists());
	}
}
