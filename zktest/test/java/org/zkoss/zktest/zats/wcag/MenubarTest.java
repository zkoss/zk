/* MenubarTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 11:42:17 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class MenubarTest extends WcagTestCase {
	@Test
	public void testHorizontal() {
		connect();

		click(jq("@menu:contains(Menu B)"));
		waitResponse();
		click(jq("@menu:contains(Menu BC)"));
		waitResponse();
		verifyA11y();
	}

	@Test
	public void testVertical() {
		connect();

		click(jq("@menu:contains(Menu B):last"));
		waitResponse();
		click(jq("@menu:contains(Menu BC):last"));
		waitResponse();
		verifyA11y();
	}
}
