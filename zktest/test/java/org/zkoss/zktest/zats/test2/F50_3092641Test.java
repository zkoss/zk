/* F50_3092641Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 16:42:06 CST 2019, Created by rudyhuang

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
public class F50_3092641Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testMesh(jq("@listitem"), "z-listitem-selected");
		testMesh(jq("@treerow"), "z-treerow-selected");
	}

	private void testMesh(JQuery item, String selectedClass) {
		click(item);
		waitResponse();
		Assert.assertTrue(item.hasClass(selectedClass));

		rightClick(item);
		waitResponse();
		Assert.assertTrue(item.hasClass(selectedClass));
		Assert.assertTrue(jq("@menupopup").isVisible());
	}
}
