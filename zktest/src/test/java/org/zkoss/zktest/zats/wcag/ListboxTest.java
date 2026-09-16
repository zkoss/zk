/* ListboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 23 14:15:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author jameschu
 */
public class ListboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		// scrollable-region-focusable: 2.1.1 is met by the grid root's tabindex="0" plus the
		// arrow-key navigation, neither of which axe can see -- it only inspects the scroller's own
		// subtree.
		verifyAxe("color-contrast", "scrollable-region-focusable");
	}
}
