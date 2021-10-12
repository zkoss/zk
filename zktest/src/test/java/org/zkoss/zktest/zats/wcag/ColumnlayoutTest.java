/* ColumnlayoutTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 10:56:16 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class ColumnlayoutTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
