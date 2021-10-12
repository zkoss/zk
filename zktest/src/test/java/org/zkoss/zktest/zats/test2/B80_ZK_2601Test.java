/* B80_ZK_2601Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 8 15:00:31 CST 2016, Created by jameschu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 *
 * @author jameschu
 */
public class B80_ZK_2601Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-spinner-icon.z-spinner-down")); //change the value in the timebox
		waitResponse(true);
		click(jq(".z-label")); //click on whatever to blur and force onChange on the timebox
		waitResponse(true);
		assertEquals(false, jq(".z-errorbox").exists());
	}
}
