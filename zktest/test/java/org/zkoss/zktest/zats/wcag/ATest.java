/* ATest.java

		Purpose:
		
		Description:
		
		History:
				Tue Jun 23 17:45:55 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class ATest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}