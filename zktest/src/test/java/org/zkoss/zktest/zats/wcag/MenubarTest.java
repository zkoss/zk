/* MenubarTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 11:42:17 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author rudyhuang
 */
public class MenubarTest extends WcagTestCase {
	/**
	 * Both tests open a menupopup, so both hit the same over-strict {@code aria-required-children}
	 * finding on {@code .z-menupopup[role=menu]} — see {@link MenupopupTest} for why it is skipped.
	 */
	private void verifyMenuA11y() {
		verifyAxe("color-contrast", "aria-required-children");
	}

	@Test
	public void testHorizontal() {
		connect();

		click(jq("@menu:contains(Menu B)"));
		waitResponse();
		click(jq("@menu:contains(Menu BC)"));
		waitResponse();
		verifyMenuA11y();
	}

	@Test
	public void testVertical() {
		connect();

		click(jq(".z-menubar-vertical @menu:contains(Menu B)"));
		waitResponse();
		click(jq(".z-menubar-vertical @menu:contains(Menu BC)"));
		waitResponse();
		verifyMenuA11y();
	}
}
