/* ShowbusyTest.java

		Purpose:
		
		Description:
		
		History:
				Thu Jul 23 16:14:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class ShowbusyTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(1));
		waitResponse();
		verifyA11y();
	}
}
