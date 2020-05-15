/* PortallayoutTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Jul 06 17:31:41 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class PortallayoutTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
