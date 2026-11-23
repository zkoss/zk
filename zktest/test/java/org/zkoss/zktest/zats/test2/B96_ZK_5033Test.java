/* B96_ZK_5033Test.java

	Purpose:
		
	Description:
		
	History:
		6:04 PM 2022/9/30, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5033Test extends WebDriverTestCase {
	@Test(timeout = 10000)
	public void testEndlessLoop() {
		connect();
	}
}
