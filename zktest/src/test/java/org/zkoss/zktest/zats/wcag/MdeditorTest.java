/* MdeditorTest.java

	Purpose:

	Description:

	History:
		Wed Jun 24 14:58:47 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

public class MdeditorTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
