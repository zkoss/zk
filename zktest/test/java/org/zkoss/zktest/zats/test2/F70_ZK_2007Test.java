/* F70_ZK_2007Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 14 12:16:17 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F70_ZK_2007Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery item = jq(".z-treerow:contains(Item)");
		JQuery item1 = jq(".z-treerow:contains(Item1)");
		JQuery item2 = jq(".z-treerow:contains(Item2)");
		rightClick(item);
		waitResponse();
		Assert.assertTrue("open the popup", jq(".z-popup").exists());

		rightClick(item);
		waitResponse();
		Assert.assertFalse("it will close", jq(".z-popup").isVisible());

		click(item1);
		waitResponse();
		Assert.assertTrue("open the context menu", jq(".z-menupopup").exists());
		click(item1);
		waitResponse();
		Assert.assertFalse("it will close", jq(".z-menupopup").isVisible());

		rightClick(item2);
		waitResponse();
		Assert.assertTrue("open the context menu", jq(".z-menupopup").exists());

		rightClick(item2);
		waitResponse();
		Assert.assertFalse("it will close", jq(".z-menupopup").isVisible());
	}
}
