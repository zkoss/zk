/* B95_ZK_4705Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 11:10:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4705Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse(3000);
		assertTrue(getZKLog().contains("MyWest bind_"));
	}
}
