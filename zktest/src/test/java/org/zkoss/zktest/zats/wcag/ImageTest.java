/* ImageTest.java

	Purpose:
		
	Description:
		
	History:
		Fri May 22 15:44:02 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class ImageTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
