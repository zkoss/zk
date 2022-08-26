/* SliderTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Jul 06 17:40:26 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

public class SliderTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
