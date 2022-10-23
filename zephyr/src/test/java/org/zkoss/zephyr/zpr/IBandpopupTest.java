/* IBandpopupTest.java

	Purpose:

	Description:

	History:
		Fri Oct 08 15:24:39 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Label;

/**
 * Test for {@link IBandpopup}
 *
 * @author katherine
 */
public class IBandpopupTest extends ZephyrTestBase {
	@Test
	public void withBandpopup() {
		// check Richlet API case
		assertEquals(richlet(() -> IBandpopup.of(getBandpopupChildren())), zul(IBandpopupTest::newBandpopup));

		// check Zephyr file case
		assertEquals(composer(IBandpopupTest::newBandpopup), zul(IBandpopupTest::newBandpopup));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Bandpopup bandpopup = new Bandpopup();
					bandpopup.appendChild(new Label("2"));
					return bandpopup;
				}, (IBandpopup<IAnyGroup> iBandpopup) -> iBandpopup.withChildren(getBandpopupChildren())),
				zul(IBandpopupTest::newBandpopup));
	}

	@Test
	public void withVisible() {
		try {
			IBandpopup.of(getBandpopupChildren()).withVisible(false);
		} catch (UnsupportedOperationException ex) {
			assertEquals("Use Bandbox.setOpen(false) instead", ex.getMessage());
		}
	}

	private static Bandpopup newBandpopup() {
		Bandpopup bandpopup = new Bandpopup();
		bandpopup.appendChild(new Label("1"));
		return bandpopup;
	}

	private static List getBandpopupChildren() {
		List children = new ArrayList();
		children.add(ILabel.of("1"));
		return children;
	}
}