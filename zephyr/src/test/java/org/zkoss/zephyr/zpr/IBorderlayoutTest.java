/* IBorderlayoutTest.java

	Purpose:

	Description:

	History:
		Wed Oct 20 09:14:46 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.East;
import org.zkoss.zul.Label;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.West;

/**
 * Test for {@link IBorderlayout}
 *
 * @author katherine
 */
public class IBorderlayoutTest extends ZephyrTestBase {
	@Test
	public void withBorderlayout() {
		ILabel ilabel = ILabel.of("abc");
		// check Richlet API case
		assertEquals(richlet(() ->
				IBorderlayout.of(INorth.of(ilabel), IWest.of(ilabel), ISouth.of(ilabel), IEast.of(ilabel), ICenter.of(ilabel))), zul(IBorderlayoutTest::newBorderlayout));

		// check Zephyr file case
		assertEquals(composer(IBorderlayoutTest::newBorderlayout), zul(IBorderlayoutTest::newBorderlayout));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Borderlayout borderlayout = new Borderlayout();
					North north = new North();
					north.appendChild(new Label("123"));
					borderlayout.appendChild(north);
					return borderlayout;
				}, (IBorderlayout iBorderlayout) ->
						iBorderlayout.withNorth(INorth.of(ilabel))),
				zul(IBorderlayoutTest::newBorderlayout2));
	}

	@Test
	public void withTwoNorth() {
		ILabel ilabel = ILabel.of("abc");
		try {
			IBorderlayout.of(INorth.of(ilabel), INorth.of(ilabel));
		} catch (UiException ex) {
			assertEquals("Only one north child is allowed.", ex.getMessage());
		}
	}

	private static Borderlayout newBorderlayout() {
		Borderlayout borderlayout = new Borderlayout();
		North north = new North();
		West west = new West();
		South south = new South();
		East east = new East();
		Center center = new Center();
		north.appendChild(new Label("abc"));
		west.appendChild(new Label("abc"));
		south.appendChild(new Label("abc"));
		east.appendChild(new Label("abc"));
		center.appendChild(new Label("abc"));
		borderlayout.appendChild(north);
		borderlayout.appendChild(west);
		borderlayout.appendChild(center);
		borderlayout.appendChild(east);
		borderlayout.appendChild(south);
		return borderlayout;
	}

	private static Borderlayout newBorderlayout2() {
		Borderlayout borderlayout = new Borderlayout();
		North north = new North();
		north.appendChild(new Label("abc"));
		borderlayout.appendChild(north);
		return borderlayout;
	}
}