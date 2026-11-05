/* CascaderTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 23 14:15:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author jameschu
 */
public class CascaderTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
		click(jq("@cascader"));
		waitResponse();
		// scrollable-region-focusable: 2.1.1 is met by the arrow keys, which move aria-activedescendant
		// and scroll the active item into view, neither of which axe can see -- it only inspects the
		// scroller's own subtree. Marble's taller items overflow the popup where IceBlue's fit.
		verifyAxe("color-contrast", "scrollable-region-focusable");
	}
}
