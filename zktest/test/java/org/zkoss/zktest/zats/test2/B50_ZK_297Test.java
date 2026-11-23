/* B50_ZK_297Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 17:51:05 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_297Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem").eq(1));
		waitResponse();
		Assert.assertTrue(jq(".z-messagebox-window").exists());
		
		click(jq(".z-messagebox-button"));
		waitResponse();
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-label"));
		waitResponse();
		Assert.assertFalse(jq(".z-messagebox-window").exists());
		Assert.assertFalse(jq(".z-errorbox").exists());
	}
}
