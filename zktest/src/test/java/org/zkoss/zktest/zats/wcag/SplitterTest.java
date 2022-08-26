/* SplitterTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 25 18:20:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author jameschu
 */
public class SplitterTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
