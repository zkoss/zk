/* B85_ZK_3313Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 12 19:02:53 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3313Test extends WebDriverTestCase {
	@Test
	public void testListbox() throws Exception {
		connect();

		JQuery elems = jq("@listbox");
		int minWidth = elems.eq(0).width();
		Assert.assertTrue("The width is too small.", elems.eq(1).width() > minWidth);
		Assert.assertTrue("The width is too small.", elems.eq(2).width() > minWidth);
	}

	@Test
	public void testGrid() throws Exception {
		connect();

		JQuery elems = jq("@grid");
		int minWidth = elems.eq(0).width();
		Assert.assertTrue("The width is too small.", elems.eq(1).width() > minWidth);
		Assert.assertTrue("The width is too small.", elems.eq(2).width() > minWidth);
	}

	@Test
	public void testTree() throws Exception {
		connect();

		JQuery elems = jq("@tree");
		int minWidth = elems.eq(0).width();
		Assert.assertTrue("The width is too small.", elems.eq(1).width() > minWidth);
	}
}
