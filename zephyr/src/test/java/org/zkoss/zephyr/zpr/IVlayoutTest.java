/* IVlayoutTest.java

	Purpose:

	Description:

	History:
		Wed Oct 20 17:08:41 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

/**
 * Test for {@link IVlayout}
 *
 * @author katherine
 */
public class IVlayoutTest extends ZephyrTestBase {
	@Test
	public void withVlayout() {
		// check Richlet API case
		assertEquals(richlet(() -> IVlayout.of(ILabel.of("abc"))), zul(IVlayoutTest::newVlayout));

		// check Zephyr file case
		assertEquals(composer(IVlayoutTest::newVlayout), zul(IVlayoutTest::newVlayout));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Vlayout vlayout =  new Vlayout();
					vlayout.appendChild(new Label("123"));
					return vlayout;
				}, (IVlayout<IAnyGroup> iVlayout) -> iVlayout.withChildren(ILabel.of("abc"))),
				zul(IVlayoutTest::newVlayout));
	}

	private static Vlayout newVlayout() {
		Vlayout vlayout =  new Vlayout();
		vlayout.appendChild(new Label("abc"));
		return vlayout;
	}
}