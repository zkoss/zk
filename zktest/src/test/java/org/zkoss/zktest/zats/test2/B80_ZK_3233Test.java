/* B80_ZK_3233Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 15:31:09 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B80_ZK_3233Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		sendKeys(jq(".z-datebox-input"), "a");
		waitResponse();
		sendKeys(jq(".z-textbox"), "a");
		waitResponse();
		click(jq(".z-label"));
		waitResponse();
		Assert.assertEquals(2, jq(".z-errorbox-open").length());
		click(jq(".z-tab:eq(1)"));
		waitResponse();
		click(jq(".z-tab:eq(0)"));
		waitResponse();
		Assert.assertEquals(2, jq(".z-errorbox-open").length());
	}
}
