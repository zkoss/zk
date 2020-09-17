/* FreezeTest.java

		Purpose:
		
		Description:
		
		History:
				Thu Sep 17 12:41:26 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class FreezeTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}