/* NotificationTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 21 09:54:18 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author rudyhuang
 */
public class NotificationTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		click(jq("@button:eq(2)"));
		waitResponse();

		verifyA11y();
	}
}
