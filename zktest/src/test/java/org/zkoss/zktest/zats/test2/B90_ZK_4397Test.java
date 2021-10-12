/* B90_ZK_4397Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 17 10:24:43 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4397Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btn = jq("@button");
		click(btn);
		waitResponse();
		click(btn);
		waitResponse();
		assertEquals(5, jq("@goldenpanel").length());
	}
}
