/* SplitlayoutTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 26 18:25:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author jameschu
 */
public class SplitlayoutTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
