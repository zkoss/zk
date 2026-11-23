/* B70_ZK_2988Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 17:18:58 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2988Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		assertNoJSError();
	}
}
