/* B80_ZK_2970Test.java

	Purpose:
		
	Description:
		
	History:
		Sat Jun  4 14:00:31 CST 2016, Created by Chris

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 *
 * @author Chris
 */
public class B80_ZK_2970Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// click the textbox, which is inside a listitem
		click(jq(".z-textbox:eq(0)"));
		waitResponse(true);
		// make sure the focus is still on the textbox, not on the listitem
		assertTrue(jq(".z-textbox:eq(0):focus").exists());
	}
}
