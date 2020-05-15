/* StepbarTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 12:50:04 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class StepbarTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();

		click(jq("@step:contains(Step 4)"));
		waitResponse();
		verifyA11y();
	}
}
