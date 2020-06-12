/* JasperreportTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 11:40:04 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class JasperreportTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
