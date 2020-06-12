/* MenupopupTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 11:45:31 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class MenupopupTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:first"));
		waitResponse();

		click(jq("@menu:contains(Menu B)"));
		waitResponse();

		verifyA11y();
	}
}
