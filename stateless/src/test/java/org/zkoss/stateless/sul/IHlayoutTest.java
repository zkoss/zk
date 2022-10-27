/* IHlayoutTest.java

	Purpose:

	Description:

	History:
		Wed Oct 20 17:08:18 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;

/**
 * Test for {@link IHlayout}
 *
 * @author katherine
 */
public class IHlayoutTest extends StatelessTestBase {
	@Test
	public void withHlayout() {
		// check Richlet API case
		assertEquals(richlet(() -> IHlayout.of(ILabel.of("abc"))), zul(IHlayoutTest::newHlayout));

		// check Stateless file case
		assertEquals(composer(IHlayoutTest::newHlayout), zul(IHlayoutTest::newHlayout));

		// check Stateless file and then recreate another immutable case
		assertEquals(thenComposer(() -> {
			Hlayout hlayout = new Hlayout();
			hlayout.appendChild(new Label("123"));
			return hlayout;
		}, (IHlayout<IAnyGroup> iHlayout) -> iHlayout.withChildren(ILabel.of("abc"))), zul(IHlayoutTest::newHlayout));
	}

	private static Hlayout newHlayout() {
		Hlayout hlayout = new Hlayout();
		hlayout.appendChild(new Label("abc"));
		return hlayout;
	}
}