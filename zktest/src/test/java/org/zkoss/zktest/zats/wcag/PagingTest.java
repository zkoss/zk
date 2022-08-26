/* PagingTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 12:47:37 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author rudyhuang
 */
public class PagingTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
