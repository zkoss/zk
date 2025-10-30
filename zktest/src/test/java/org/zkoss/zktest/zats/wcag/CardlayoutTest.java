/* CardlayoutTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Jun 23 18:05:57 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

public class CardlayoutTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}