/* ICombobuttonTest.java

	Purpose:

	Description:

	History:
		Fri Oct 08 18:07:23 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Combobutton;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link ICombobutton}
 *
 * @author katherine
 */
public class ICombobuttonTest extends StatelessTestBase {
	@Test
	public void withCombobutton() {
		// check Richlet API case
		List l = new ArrayList();
		l.add(ILabel.of("1"));
		assertEquals(richlet(() -> ICombobutton.of("abc", IPopup.of(l))), zul(ICombobuttonTest::newCombobutton));

		// check Stateless file case
		assertEquals(composer(ICombobuttonTest::newCombobutton), zul(ICombobuttonTest::newCombobutton));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Combobutton combobutton = new Combobutton("abc");
					Popup popup = new Popup();
					popup.appendChild(new Label("1"));
					combobutton.appendChild(popup);
					return combobutton;
				}, (ICombobutton iCombobutton) -> iCombobutton.withLabel("abc")),
				zul(ICombobuttonTest::newCombobutton));
	}

	@Test
	public void withTwoChild() {
		List l = new ArrayList();
		l.add(ILabel.of("1"));
		List l2 = new ArrayList();
		l.add(ILabel.of("2"));
		IPopup iPopup = IPopup.of(l);
		IPopup iPopup2 = IPopup.of(l2);

		try {
			ICombobutton.of("abc", iPopup2).withChild(iPopup);
		} catch (UiException ex) {
			assertEquals("At most one popup is allowed.", ex.getMessage());
		}
	}

	private static Combobutton newCombobutton() {
		Combobutton combobutton = new Combobutton("abc");
		Popup popup = new Popup();
		popup.appendChild(new Label("1"));
		combobutton.appendChild(popup);
		return combobutton;
	}
}