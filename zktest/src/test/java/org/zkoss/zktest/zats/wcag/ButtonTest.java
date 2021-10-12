/* ButtonTest.java

	Purpose:
		
	Description:
		
	History:
		Fri May 22 17:29:31 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class ButtonTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
