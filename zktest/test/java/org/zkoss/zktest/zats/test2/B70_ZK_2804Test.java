/* B70_ZK_2804Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 14:35:53 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2804Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@listitem:eq(0)"));

	}
}
