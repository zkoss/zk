/* IPopupTest.java

	Purpose:

	Description:

	History:
		Fri Oct 08 18:10:49 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

/**
 * Test for {@link IPopup}
 *
 * @author katherine
 */
public class IPopupTest extends StatelessTestBase {
	@Test
	public void withPopup() {
		// check Richlet API case
		List children = new ArrayList();
		children.add(ILabel.of("1"));
		assertEquals(richlet(() -> IPopup.of(children)), zul(IPopupTest::newPopup));

		// check Stateless file case
		assertEquals(composer(IPopupTest::newPopup), zul(IPopupTest::newPopup));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Popup popup = new Popup();
					popup.appendChild(new Label("2"));
					return popup;
				}, (IPopup<IAnyGroup> iPopup) -> iPopup.withChildren(children)),
				zul(IPopupTest::newPopup));
	}

	@Test
	public void withVisible() {
		try {
			List list = new ArrayList();
			list.add(ILabel.of("1"));
			IPopup.of(list).withVisible(true);
		} catch (UnsupportedOperationException ex) {
			assertEquals("Use open/close instead", ex.getMessage());
		}
	}

	private static Popup newPopup() {
		Popup popup = new Popup();
		popup.appendChild(new Label("1"));
		return popup;
	}
}