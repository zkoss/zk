/* TabboxTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 12:52:22 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class TabboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();

		click(jq("@tab:contains(Tab D):eq(0)"));
		waitResponse(true);
		click(jq("@tab:contains(Tab D):eq(1)"));
		waitResponse(true);
		click(jq("@tab:contains(Tab D):eq(2)"));
		waitResponse(true);
		verifyA11y();
	}
}
