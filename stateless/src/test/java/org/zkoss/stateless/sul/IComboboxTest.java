/* IComboboxTest.java

	Purpose:

	Description:

	History:
		Fri Oct 15 13:09:59 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link ICombobox}
 *
 * @author katherine
 */
public class IComboboxTest extends StatelessTestBase {
	@Test
	public void withCombobox() {
		// check Richlet API case
		assertEquals(richlet(() -> ICombobox.of(IComboitem.of("abc"))), zul(IComboboxTest::newCombobox));

		// check Stateless file case
		assertEquals(composer(IComboboxTest::newCombobox), zul(IComboboxTest::newCombobox));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Combobox combobox =  new Combobox();
					combobox.appendChild(new Comboitem("123"));
					return combobox;
				}, (ICombobox iCombobox) -> iCombobox.withChildren(IComboitem.of("abc"))),
				zul(IComboboxTest::newCombobox));
	}

	private static Combobox newCombobox() {
		Combobox combobox =  new Combobox();
		combobox.appendChild(new Comboitem("abc"));
		return combobox;
	}
}