/* BorderlayoutTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Jun 23 18:04:05 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class BorderlayoutTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}