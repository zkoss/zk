/* PopupTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 15:05:59 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author rudyhuang
 */
public class PopupTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		verifyA11y();
	}
}
