/* F80_ZK_3166Test.java

	Purpose:
		
	Description:
		
	History:
		Sat Jun  4 14:00:31 CST 2016, Created by Chris

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
 * @author Chris
 */
public class F80_ZK_3166Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//this test starts with multiple animation, waitResponse doesn't wait long enough, must use sleep
		sleep(5000);
		assertEquals(2, getZKLog().split("after setSlide").length);
		click(jq(".z-west-collapsed"));
		waitResponse(true);
		assertEquals(3, getZKLog().split("after setSlide").length);
		click(jq(".z-west-collapsed"));
		waitResponse(true);
		assertEquals(4, getZKLog().split("after setSlide").length);
		click(jq(".z-button:contains(\"down\")"));
		waitResponse(true);
		assertEquals(5, getZKLog().split("after setSlide").length);
		click(jq(".z-button:contains(\"up\")"));
		waitResponse(true);
		assertEquals(7, getZKLog().split("after setSlide").length);
	}
}
