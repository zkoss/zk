/* LoadingbarTest.java

		Purpose:
		
		Description:
		
		History:
				Wed Jul 22 12:49:59 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class LoadingbarTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}