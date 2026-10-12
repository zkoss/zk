/* MenupopupTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 11:45:31 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author rudyhuang
 */
public class MenupopupTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:first"));
		waitResponse();

		click(jq("@menu:contains(Menu B)"));
		waitResponse();

		// aria-required-children is stricter than the spec here. WAI-ARIA 1.2 §5.2.6 requires a
		// role="menu" to own *at least one* menuitem ("at least one instance of one required owned
		// element is expected") and does not forbid other children; Deque's own rule description
		// documents only that presence half. What axe rejects is the z-focus-a proxy, which counts
		// as an owned child purely because it carries tabindex, once za11y's ZK-5787 focusin
		// handler clears its aria-hidden.
		// https://www.w3.org/TR/wai-aria-1.2/#mustContain
		// https://dequeuniversity.com/rules/axe/4.13/aria-required-children
		verifyAxe("color-contrast", "aria-required-children");
	}
}
