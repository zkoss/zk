/* AnchornavTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 18:37:55 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class AnchornavTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@a:contains(Genesis 2)"));
		waitResponse();

		verifyA11y();
	}
}
