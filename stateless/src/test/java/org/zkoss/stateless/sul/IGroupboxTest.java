/* IGroupboxTest.java

	Purpose:

	Description:

	History:
		Wed Oct 20 18:02:25 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Groupbox;

/**
 * Test for {@link IGroupbox}
 *
 * @author katherine
 */
public class IGroupboxTest extends StatelessTestBase {
	@Test
	public void withGroupbox() {
		// check Richlet API case
		assertEquals(richlet(() -> IGroupbox.ofCaption(ICaption.of("abc"))), zul(IGroupboxTest::newGroupbox));

		// check Stateless file case
		assertEquals(composer(IGroupboxTest::newGroupbox), zul(IGroupboxTest::newGroupbox));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Groupbox groupbox = new Groupbox();
					groupbox.appendChild(new Caption("123"));
					return groupbox;
				}, (IGroupbox<IAnyGroup> iGroupbox) -> iGroupbox.withCaption(ICaption.of("abc"))),
				zul(IGroupboxTest::newGroupbox));
	}

	@Test
	public void withCaption() {
		try {
			IGroupbox.of(ICaption.of("abc"), ICaption.of("abc"));
		} catch (UiException ex) {
			assertEquals("caption must be the first child and only one caption is allowed.", ex.getMessage());
		}
	}

	private static Groupbox newGroupbox() {
		Groupbox groupbox = new Groupbox();
		groupbox.appendChild(new Caption("abc"));
		return groupbox;
	}
}