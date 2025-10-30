/* MessageboxTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 6 14:26:02 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author jameschu
 */
public class MessageboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 4; i++) {
			click(jq("@button").eq(i));
			waitResponse();
			verifyA11y();
			click(jq(".z-window").find("@button"));
			waitResponse();
		}
	}
}
