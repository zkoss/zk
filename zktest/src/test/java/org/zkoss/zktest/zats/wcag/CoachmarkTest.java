/* CoachmarkTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 10:49:45 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class CoachmarkTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:first"));
		waitResponse();

		verifyA11y();
	}
}
