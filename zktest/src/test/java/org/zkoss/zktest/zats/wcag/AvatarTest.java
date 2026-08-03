/* AvatarTest.java

	Purpose:

	Description:

	History:
		Thu May 14 15:20:47 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

public class AvatarTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
