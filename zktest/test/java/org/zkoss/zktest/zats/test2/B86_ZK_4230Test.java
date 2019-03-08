/* B86_ZK_4230Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 07 12:56:00 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4230Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery toolbar = jq(".z-signature-toolbar");
		JQuery toggle = jq(".z-button");
		Assert.assertFalse(toolbar.isVisible());
		click(toggle);
		waitResponse();
		Assert.assertTrue(toolbar.isVisible());
		click(toggle);
		waitResponse();
		Assert.assertFalse(toolbar.isVisible());
	}
}
